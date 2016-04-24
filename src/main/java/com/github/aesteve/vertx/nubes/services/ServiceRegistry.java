package com.github.aesteve.vertx.nubes.services;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.services.Consumer;
import com.github.aesteve.vertx.nubes.annotations.services.PeriodicTask;
import com.github.aesteve.vertx.nubes.annotations.services.Proxify;
import com.github.aesteve.vertx.nubes.annotations.services.ServiceProxy;
import com.github.aesteve.vertx.nubes.utils.async.MultipleFutures;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ProxyHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ServiceRegistry {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistry.class);

  private final Map<String, Object> services;
  private final Map<String, Object> serviceProxies;
  private final Set<Long> timerIds;
  private final Config config;

  private final Vertx vertx;

  public ServiceRegistry(Vertx vertx, Config config) {
    this.vertx = vertx;
    this.config = config;
    services = new HashMap<>();
    serviceProxies = new HashMap<>();
    timerIds = new HashSet<>();
  }

  public void registerService(String name, Object service) {
    services.put(name, service);
  }

  public Object get(String name) {
    return services.get(name);
  }

  public Object get(Field field) {
    com.github.aesteve.vertx.nubes.annotations.services.Service annot = field.getAnnotation(com.github.aesteve.vertx.nubes.annotations.services.Service.class);
    if (annot != null) {
      return get(annot.value());
    }
    ServiceProxy proxyAnnot = field.getAnnotation(ServiceProxy.class);
    if (proxyAnnot == null) {
      return null;
    }
    Class<?> serviceInterface = getInterface(field.getType());
    if (serviceInterface == null) {
      LOG.error("Could not inject service for : " + field.getName() + " could not find the matching proxified service using @ProxyGen");
      return null;
    }
    String address = proxyAnnot.value();
    if (serviceProxies.get(address) != null) {
      return serviceProxies.get(address);
    } else {
      Object service = createEbProxyClass(serviceInterface, address);
      serviceProxies.put(address, service);
      return service;
    }
  }

  public Collection<Object> services() {
    return services.values();
  }

  public boolean isEmpty() {
    return services.isEmpty();
  }

  public void startAll(Future<Void> future) {
    if (isEmpty()) {
      future.complete();
      return;
    }
    MultipleFutures<Void> futures = new MultipleFutures<>(future);
    futures.addAll(services(), obj -> {
      try {
        introspectService(obj);
      } catch (Exception e) {
        return res -> res.fail(e);
      }
      if (obj instanceof Service) {
        Service service = (Service) obj;
        service.init(vertx, config.json());
        return service::start;
      }
      return null;
    });
    futures.start();
  }

  public void stopAll(Future<Void> future) {
    if (isEmpty()) {
      future.complete();
      return;
    }
    timerIds.forEach(vertx::cancelTimer);
    MultipleFutures<Void> futures = new MultipleFutures<>(future);
    futures.addAll(services(), obj -> {
      if (obj instanceof Service) {
        Service service = (Service) obj;
        return service::stop;
      }
      return null;
    });
    futures.start();
  }

  private void introspectService(Object service) {
    Class<?> serviceClass = service.getClass();
    Proxify annot = serviceClass.getAnnotation(Proxify.class);
    if (annot != null) {
      createServiceProxy(annot.value(), service);
    }
    for (Method method : service.getClass().getMethods()) {
      PeriodicTask periodicTask = method.getAnnotation(PeriodicTask.class);
      if (periodicTask != null) {
        createPeriodicTask(service, periodicTask, method);
      }
      Consumer consumes = method.getAnnotation(Consumer.class);
      if (consumes != null) {
        createConsumer(service, consumes, method);
      }
    }
  }

  private void createConsumer(Object service, Consumer consumes, Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 1 || !parameterTypes[0].equals(Message.class)) {
      String msg = "Cannot register consumer on method : " + getFullName(service, method);
      msg += " .Method should only declare one parameter of io.vertx.core.eventbus.Message type.";
      throw new VertxException(msg);
    }
    vertx.eventBus().consumer(consumes.value(), message -> {
      try {
        method.invoke(service, message);
      } catch (Exception e) {
        LOG.error("Exception happened during message handling on method : " + getFullName(service, method), e);
      }
    });
  }

  private void createPeriodicTask(Object service, PeriodicTask annotation, Method method) {
    if (method.getParameterTypes().length > 0) {
      throw new VertxException("Periodic tasks should not have parameters");
    }
    vertx.setPeriodic(annotation.value(), timerId -> {
      timerIds.add(timerId);
      try {
        method.invoke(service);
      } catch (Exception e) {
        LOG.error("Error while running periodic task", e);
      }
    });
  }

  private static String getFullName(Object service, Method method) {
    return service.getClass().getName() + "." + method.getName();
  }

  private <T> void createServiceProxy(String address, T service) {
    Class<T> serviceClass = getInterface(service.getClass());
    if (serviceClass == null) {
      LOG.error("Could not find a @ProxyGen super interface for class : " + service.getClass().getName() + " cannot proxy it ver the eventBus");
      return;
    }
    ProxyHelper.registerService(serviceClass, vertx, service, address);
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getInterface(Class<?> serviceClass) {
    if (serviceClass.isAnnotationPresent(ProxyGen.class)) {
      return (Class<T>) serviceClass;
    }
    Class<?>[] interfaces = serviceClass.getInterfaces();
    for (Class<?> someInterface : interfaces) {
      if (someInterface.isAnnotationPresent(ProxyGen.class)) { // it must be it
        return (Class<T>) someInterface;
      }
    }
    return null;
  }

  public Object createEbProxyClass(Class<?> serviceInterface, String address) {
    String name = serviceInterface.getName() + "VertxEBProxy";
    try {
      return Class.forName(name).getConstructor(Vertx.class, String.class).newInstance(vertx, address);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
      throw new VertxException("Could not create your service proxy for class : " + serviceInterface, e);
    }
  }
}
