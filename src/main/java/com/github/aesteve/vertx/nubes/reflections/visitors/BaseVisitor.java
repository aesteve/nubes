package com.github.aesteve.vertx.nubes.reflections.visitors;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.services.ServiceRegistry;
import io.vertx.ext.web.Router;

import java.lang.reflect.Field;

abstract class BaseVisitor<T> {

	protected T instance;
	protected final Class<T> clazz;
	protected final Router router;
	protected final Config config;
	protected String path;

	BaseVisitor(Class<T> clazz, Config config, Router router) {
		this.clazz = clazz;
		this.config = config;
		this.router = router;
	}

	void injectServices() throws IllegalAccessException {
		final ServiceRegistry serviceRegistry = config.getServiceRegistry();
		for (Field field : instance.getClass().getDeclaredFields()) {
			Object service = serviceRegistry.get(field);
			setFieldAccessible(router, instance, service, field);
		}
		for (Field field : instance.getClass().getSuperclass().getDeclaredFields()) {
			Object service = serviceRegistry.get(field);
			setFieldAccessible(router, instance, service, field);
		}
	}

	private void setFieldAccessible(Router router, Object instance, Object service, Field field) throws IllegalAccessException {
		if (service != null) {
			field.setAccessible(true);
			field.set(instance, service);
		} else if (field.getType().equals(Router.class)) {
			field.setAccessible(true);
			field.set(instance, router);
		}

	}

	void normalizePath() {
		if (!path.endsWith("/*")) {
			if (path.endsWith("/")) {
				path += "*";
			} else {
				path += "/*";
			}
		}
	}
}
