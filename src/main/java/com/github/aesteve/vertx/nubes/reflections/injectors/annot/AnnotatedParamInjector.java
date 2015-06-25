package com.github.aesteve.vertx.nubes.reflections.injectors.annot;

import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;

public interface AnnotatedParamInjector<T extends Annotation> {
	public Object resolve(RoutingContext context, T annotation, Class<?> resultClass);
}
