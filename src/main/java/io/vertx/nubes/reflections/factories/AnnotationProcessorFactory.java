package io.vertx.nubes.reflections.factories;

import io.vertx.nubes.handlers.AnnotationProcessor;

import java.lang.annotation.Annotation;

public interface AnnotationProcessorFactory<T extends Annotation> {
    public AnnotationProcessor<T> create(T annotation);
}
