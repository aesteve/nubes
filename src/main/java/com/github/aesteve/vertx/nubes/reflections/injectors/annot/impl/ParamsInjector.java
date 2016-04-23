package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.exceptions.params.InvalidParamValueException;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class ParamsInjector implements AnnotatedParamInjector<Params> {

	private final ParameterAdapterRegistry adapters;

	public ParamsInjector(ParameterAdapterRegistry adapters) {
		this.adapters = adapters;
	}

	@Override
	public Object resolve(RoutingContext context, Params annotation, String paramName, Class<?> resultClass) throws WrongParameterException {
		try {
			return adapters.adaptParams(context.request().params(), resultClass);
		} catch (IllegalArgumentException iae) {
			throw new InvalidParamValueException(null, null, null, iae);
		}
	}

}
