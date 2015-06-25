package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.Header;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class HeaderParamInjector implements AnnotatedParamInjector<Header> {

	private ParameterAdapterRegistry registry;

	public HeaderParamInjector(ParameterAdapterRegistry registry) {
		this.registry = registry;
	}

	@Override
	public Object resolve(RoutingContext context, Header annotation, Class<?> resultClass) {
		String headerValue = context.request().getHeader(annotation.value());
		if (headerValue == null) {
			if (annotation.mandatory()) {
				DefaultErrorHandler.badRequest(context, "Header : " + headerValue + " is not set");
			}
			return null;
		}
		try {
			return registry.adaptParam(headerValue, resultClass);
		} catch (Exception e) {
			DefaultErrorHandler.badRequest(context, "Wrong value for header : " + annotation.value());
			return null;
		}
	}

}
