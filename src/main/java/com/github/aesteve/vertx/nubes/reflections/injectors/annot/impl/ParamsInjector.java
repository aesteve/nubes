package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class ParamsInjector implements AnnotatedParamInjector<Params> {

	private ParameterAdapterRegistry adapters;

	public ParamsInjector(ParameterAdapterRegistry adapters) {
		this.adapters = adapters;
	}

	@Override
	public Object resolve(RoutingContext context, Params annotation, Class<?> resultClass) {
		try {
			return adapters.adaptParams(context.request().params(), resultClass);
		} catch (Exception e) {
			DefaultErrorHandler.badRequest(context, "Some request parameter (or form parameter) has a wrong value");
			return null;
		}
	}

}
