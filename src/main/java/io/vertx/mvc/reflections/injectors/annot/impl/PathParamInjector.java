package io.vertx.mvc.reflections.injectors.annot.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.params.PathParam;
import io.vertx.mvc.exceptions.BadRequestException;
import io.vertx.mvc.reflections.adapters.ParameterAdapterRegistry;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjector;

public class PathParamInjector implements AnnotatedParamInjector<PathParam> {

	private ParameterAdapterRegistry adapters;
	
	public PathParamInjector(ParameterAdapterRegistry adapters) {
		this.adapters = adapters;
	}
	
	@Override
	public Object resolve(RoutingContext context, PathParam annotation, Class<?> resultClass) throws BadRequestException {
		try {
			String paramValue = context.request().getParam(annotation.value());
			return adapters.adaptParam(paramValue, resultClass);
		} catch(Exception e) {
			throw new BadRequestException("Cannot read route param : "+annotation.value(), e);
		}
			
	}

}
