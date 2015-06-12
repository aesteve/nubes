package com.github.aesteve.vertx.nubes.services;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.utils.async.MultipleFutures;

public class ServiceRegistry {

    private Map<Class<?>, Object> services;

    private Vertx vertx;

    public ServiceRegistry(Vertx vertx) {
        this.vertx = vertx;
        services = new HashMap<Class<?>, Object>();
    }

    public void registerService(Object service) {
        services.put(service.getClass(), service);
    }

    public Object get(Class<?> serviceClass) {
        return services.get(serviceClass);
    }

    public Object get(Field field) {
        Class<?> clazz = field.getType();
        return get(clazz);
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
        MultipleFutures futures = new MultipleFutures(future);
        services().forEach(obj -> {
            if (obj instanceof Service) {
                Service service = (Service) obj;
                service.init(vertx);
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
        MultipleFutures futures = new MultipleFutures(future);
        services().forEach(obj -> {
            if (obj instanceof Service) {
                Service service = (Service) obj;
                futures.add(service::stop);
            }
        });
        futures.start();
    }
}
