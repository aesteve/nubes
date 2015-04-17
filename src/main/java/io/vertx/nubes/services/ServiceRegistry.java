package io.vertx.nubes.services;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.nubes.utils.MultipleFutures;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
    private Map<Class<? extends Service>, Service> services;

    private Vertx vertx;

    public ServiceRegistry(Vertx vertx) {
        this.vertx = vertx;
        services = new HashMap<Class<? extends Service>, Service>();
    }

    public void registerService(Service service) {
        services.put(service.getClass(), service);
    }

    public Service get(Class<? extends Service> serviceClass) {
        return services.get(serviceClass);
    }

    @SuppressWarnings("unchecked")
    public Service get(Field field) {
        Class<? extends Service> clazz = (Class<? extends Service>) field.getType();
        return get(clazz);
    }

    public Collection<Service> services() {
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
        MultipleFutures<Void> multFuture = new MultipleFutures<Void>();
        Map<Service, Future<Void>> futures = new HashMap<Service, Future<Void>>();
        services().forEach(service -> {
            Future<Void> serviceFuture = Future.future();
            futures.put(service, serviceFuture);
            multFuture.addFuture(serviceFuture);
        });
        multFuture.setHandler(res -> {
            if (res.succeeded()) {
                future.complete();
            } else {
                future.fail(res.cause());
            }
        });
        services().forEach(service -> {
            Future<Void> serviceFuture = futures.get(service);
            service.init(vertx);
            service.start(serviceFuture);
        });
    }

    public void stopAll(Future<Void> future) {
        if (isEmpty()) {
            future.complete();
            return;
        }
        MultipleFutures<Void> multFuture = new MultipleFutures<Void>();
        Map<Service, Future<Void>> futures = new HashMap<Service, Future<Void>>();
        services().forEach(service -> {
            Future<Void> serviceFuture = Future.future();
            futures.put(service, serviceFuture);
        });
        multFuture.setHandler(res -> {
            if (res.succeeded()) {
                future.complete();
            } else {
                future.fail(res.cause());
            }
        });
        services().forEach(service -> {
            Future<Void> serviceFuture = futures.get(service);
            service.stop(serviceFuture);
        });
    }
}
