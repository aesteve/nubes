package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.Headers;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class HeadersParamInjector implements AnnotatedParamInjector<Headers> {

	@Override
	public Object resolve(RoutingContext context, Headers annotation, String paramName, Class<?> resultClass) {
		if (!MultiMap.class.isAssignableFrom(resultClass)) {
			context.fail(new Exception("Could not inject @Headers to the method. Headers parameter should be a MultiMap."));
		}
		return context.request().headers();
	}

}
