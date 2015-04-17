package io.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.nubes.annotations.params.Param;
import io.vertx.nubes.exceptions.BadRequestException;
import io.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import io.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class ParamInjector implements AnnotatedParamInjector<Param> {

	private ParameterAdapterRegistry adapters;
	
	public ParamInjector(ParameterAdapterRegistry adapters) {
		this.adapters = adapters;
	}
	
	@Override
	public Object resolve(RoutingContext context, Param annotation, Class<?> resultClass) throws BadRequestException {
		String paramValue = context.request().getParam(annotation.value());
		if (paramValue == null ) {
			if (annotation.mandatory()) {
				throw new BadRequestException("Parameter "+annotation.value()+ " is mandatory");
			} else {
				return null;
			}
		}
		try {
			return adapters.adaptParam(paramValue, resultClass);
		} catch(Exception e) {
			throw new BadRequestException("Param "+annotation.value() + " is invalid", e);
		}
	}

}
