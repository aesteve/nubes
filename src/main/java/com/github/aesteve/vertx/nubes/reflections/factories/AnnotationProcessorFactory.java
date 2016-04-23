package com.github.aesteve.vertx.nubes.reflections.factories;

import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

import java.lang.annotation.Annotation;

public interface AnnotationProcessorFactory<T extends Annotation> {
  AnnotationProcessor<T> create(T annotation);
}
