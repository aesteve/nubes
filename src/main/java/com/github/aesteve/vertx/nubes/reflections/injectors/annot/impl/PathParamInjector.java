package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.PathParam;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class PathParamInjector implements AnnotatedParamInjector<PathParam> {

    private ParameterAdapterRegistry adapters;

    public PathParamInjector(ParameterAdapterRegistry adapters) {
        this.adapters = adapters;
    }

    @Override
    public Object resolve(RoutingContext context, PathParam annotation, Class<?> resultClass) {
        try {
            String paramValue = context.request().getParam(annotation.value());
            return adapters.adaptParam(paramValue, resultClass);
        } catch (Exception e) {
            DefaultErrorHandler.badRequest(context, "Wrong value for parameter : " + annotation.value());
            return null;
        }

    }

}
