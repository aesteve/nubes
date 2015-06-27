package com.github.aesteve.vertx.nubes.reflections;

import java.lang.reflect.Field;

import com.github.aesteve.vertx.nubes.Config;

public abstract class AbstractInjectionFactory {

	protected Config config;

	protected void injectServicesIntoController(Object instance) throws IllegalAccessException {
		for (Field field : instance.getClass().getDeclaredFields()) {
			Object service = config.serviceRegistry.get(field);
			if (service != null) {
				field.setAccessible(true);
				field.set(instance, config.serviceRegistry.get(field));
			}
		}
	}

}
