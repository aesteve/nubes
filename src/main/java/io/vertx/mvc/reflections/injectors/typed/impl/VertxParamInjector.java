package io.vertx.mvc.reflections.injectors.typed.impl;

import io.vertx.core.Vertx;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.reflections.injectors.typed.ParamInjector;

public class VertxParamInjector implements ParamInjector<Vertx> {

	@Override
	public Vertx resolve(RoutingContext context) {
		return context.vertx();
	}

}
