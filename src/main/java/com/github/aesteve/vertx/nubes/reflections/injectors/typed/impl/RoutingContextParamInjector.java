package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

public class RoutingContextParamInjector implements ParamInjector<RoutingContext> {

	@Override
	public RoutingContext resolve(RoutingContext context) {
		return context;
	}

}
