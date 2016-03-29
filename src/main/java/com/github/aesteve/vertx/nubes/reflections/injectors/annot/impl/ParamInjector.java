package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.exceptions.params.InvalidParamValueException;
import com.github.aesteve.vertx.nubes.exceptions.params.MandatoryParamException;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException.ParamType;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class ParamInjector implements AnnotatedParamInjector<Param> {

	private final ParameterAdapterRegistry adapters;

	public ParamInjector(ParameterAdapterRegistry adapters) {
		this.adapters = adapters;
	}

	@Override
	public Object resolve(RoutingContext context, Param annotation, String paramName, Class<?> resultClass) throws WrongParameterException {
		String requestParamName = annotation.value();
		if ("".equals(requestParamName)) {
			requestParamName = paramName;
		}
		String paramValue = context.request().getParam(requestParamName);
		if (paramValue == null) {
			if (annotation.mandatory()) {
				throw new MandatoryParamException(ParamType.REQUEST_PARAM, requestParamName);
			}
		}
		try {
			return adapters.adaptParam(paramValue, resultClass);
		} catch (Exception e) {
			throw new InvalidParamValueException(ParamType.REQUEST_PARAM, requestParamName, paramValue);
		}
	}

}
