package com.github.aesteve.vertx.nubes.reflections.injectors.typed;

import io.vertx.ext.web.RoutingContext;

@FunctionalInterface
public interface ParamInjector<T> {
	T resolve(RoutingContext context);
}
