package io.vertx.nubes.handlers;

import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;

public interface AnnotationProcessor<T extends Annotation> extends Processor {
    public void init(RoutingContext context, T annotation);

    public Class<? extends T> getAnnotationType();
}
