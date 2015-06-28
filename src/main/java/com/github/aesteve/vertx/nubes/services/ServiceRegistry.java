package com.github.aesteve.vertx.nubes.services;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.aesteve.vertx.nubes.annotations.services.Consumer;
import com.github.aesteve.vertx.nubes.annotations.services.PeriodicTask;
import com.github.aesteve.vertx.nubes.utils.async.MultipleFutures;

public class ServiceRegistry {

	private final static Logger log = LoggerFactory.getLogger(ServiceRegistry.class);

	private Map<String, Object> services;
	private Set<Long> timerIds;

	private Vertx vertx;

	public ServiceRegistry(Vertx vertx) {
		this.vertx = vertx;
		services = new HashMap<>();
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
		return null;
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
		services().forEach(obj -> {
			if (obj instanceof Service) {
				Service service = (Service) obj;
				service.init(vertx);
				introspectService(service);
				futures.add(service::start);
			}
		});
		futures.start();
	}

	public void stopAll(Future<Void> future) {
		if (isEmpty()) {
			future.complete();
			return;
		}
		timerIds.forEach(timerId -> {
			vertx.cancelTimer(timerId);
		});
		MultipleFutures<Void> futures = new MultipleFutures<>(future);
		services().forEach(obj -> {
			if (obj instanceof Service) {
				Service service = (Service) obj;
				futures.add(service::stop);
			}
		});
		futures.start();
	}

	private void introspectService(Service service) {
		for (Method method : service.getClass().getMethods()) {
			PeriodicTask periodicTask = method.getAnnotation(PeriodicTask.class);
			if (periodicTask != null) {
				if (method.getParameterTypes().length > 0) {
					log.error("Periodic tasks should not have parameters");
					return;
				}
				vertx.setPeriodic(periodicTask.value(), timerId -> {
					timerIds.add(timerId);
					try {
						method.invoke(service);
					} catch (Exception e) {
						log.error("Error while running periodic task", e);
					}
				});
			}
			Consumer consumes = method.getAnnotation(Consumer.class);
			if (consumes != null) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length != 1 || !parameterTypes[0].equals(Message.class)) {
					log.error("Cannot register consumer on method : " + getFullName(service, method));
					log.error("Method should only declare one parameter of io.vertx.core.eventbus.Message type.");
					return;
				}
				vertx.eventBus().consumer(consumes.value(), message -> {
					try {
						method.invoke(service, message);
					} catch (Exception e) {
						log.error("Exception happened during message handling on method : " + getFullName(service, method), e);
					}
				});
			}
		}
	}

	private String getFullName(Service service, Method method) {
		return service.getClass().getName() + "." + method.getName();
	}
}
