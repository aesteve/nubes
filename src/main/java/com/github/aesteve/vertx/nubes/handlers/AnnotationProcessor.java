package com.github.aesteve.vertx.nubes.handlers;

import java.lang.annotation.Annotation;

public interface AnnotationProcessor<T extends Annotation> extends Processor {
    public Class<? extends T> getAnnotationType();
}
