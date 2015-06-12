package com.github.aesteve.vertx.nubes.reflections.injectors.annot;

import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;

import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;

public interface AnnotatedParamInjector<T extends Annotation> {
    // public<R> R resolve(RoutingContext context, T annotation, Class<R> resultClass); // FIXME : keep this version ?
    public Object resolve(RoutingContext context, T annotation, Class<?> resultClass) throws BadRequestException;
}
