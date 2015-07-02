package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.ContextData;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class ContextDataParamInjector implements AnnotatedParamInjector<ContextData> {

	@Override
	public Object resolve(RoutingContext context, ContextData annotation, Class<?> resultClass) {
		return context.data();
	}

}
