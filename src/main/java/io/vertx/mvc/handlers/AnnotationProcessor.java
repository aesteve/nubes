package io.vertx.mvc.handlers;

import java.lang.annotation.Annotation;

public interface AnnotationProcessor<T extends Annotation> extends Processor {
	public void init(T annotation);
}
