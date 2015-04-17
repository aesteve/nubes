package io.vertx.nubes.reflections.injectors.typed;

import io.vertx.ext.apex.RoutingContext;

public interface ParamInjector<T> {
	public T resolve(RoutingContext context);
}
