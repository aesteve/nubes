package io.vertx.mvc.reflections.injectors.annot.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.params.Header;
import io.vertx.mvc.exceptions.BadRequestException;
import io.vertx.mvc.reflections.adapters.ParameterAdapterRegistry;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjector;

public class HeaderParamInjector implements AnnotatedParamInjector<Header> {

	private ParameterAdapterRegistry registry;
	
	public HeaderParamInjector(ParameterAdapterRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public Object resolve(RoutingContext context, Header annotation, Class<?> resultClass) throws BadRequestException {
		String headerValue = context.request().getHeader(annotation.value());
		if(headerValue == null) {
			if (annotation.mandatory()) {
				throw new BadRequestException("Header : " + annotation.value()+ " is mandatory");
			} else {
				return null;
			}
		}
		try {
			return registry.adaptParam(headerValue, resultClass);
		} catch(Exception e) {
			throw new BadRequestException("Header : "+annotation.value() + " is invalid", e);
		}
	}
	
}
