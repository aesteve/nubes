package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.PathParam;
import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

import io.vertx.ext.web.RoutingContext;

public class PathParamInjector implements AnnotatedParamInjector<PathParam> {

    private ParameterAdapterRegistry adapters;

    public PathParamInjector(ParameterAdapterRegistry adapters) {
        this.adapters = adapters;
    }

    @Override
    public Object resolve(RoutingContext context, PathParam annotation, Class<?> resultClass) throws BadRequestException {
        try {
            String paramValue = context.request().getParam(annotation.value());
            return adapters.adaptParam(paramValue, resultClass);
        } catch (Exception e) {
            throw new BadRequestException("Cannot read route param : " + annotation.value(), e);
        }

    }

}
