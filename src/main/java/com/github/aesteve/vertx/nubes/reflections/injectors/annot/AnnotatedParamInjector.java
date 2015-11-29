package com.github.aesteve.vertx.nubes.reflections.injectors.annot;

import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;

import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;

@FunctionalInterface
public interface AnnotatedParamInjector<T extends Annotation> {
	public Object resolve(RoutingContext context, T annotation, String paramName, Class<?> resultClass) throws WrongParameterException;
}
