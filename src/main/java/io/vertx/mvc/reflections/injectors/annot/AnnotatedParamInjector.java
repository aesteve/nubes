package io.vertx.mvc.reflections.injectors.annot;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.exceptions.BadRequestException;

import java.lang.annotation.Annotation;

public interface AnnotatedParamInjector<T extends Annotation> {
    // public<R> R resolve(RoutingContext context, T annotation, Class<R> resultClass); // FIXME : keep this version ?
    public Object resolve(RoutingContext context, T annotation, Class<?> resultClass) throws BadRequestException;
}
