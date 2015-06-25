package com.github.aesteve.vertx.nubes.reflections.factories;

import java.lang.annotation.Annotation;

import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

public interface AnnotationProcessorFactory<T extends Annotation> {
	public AnnotationProcessor<T> create(T annotation);
}
