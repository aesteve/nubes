package io.vertx.mvc.handlers;

import io.vertx.ext.apex.RoutingContext;

import java.lang.annotation.Annotation;

public interface AnnotationProcessor<T extends Annotation> extends Processor {
	public void init(RoutingContext context, T annotation);
	public Class<? extends T> getAnnotationType();
}
