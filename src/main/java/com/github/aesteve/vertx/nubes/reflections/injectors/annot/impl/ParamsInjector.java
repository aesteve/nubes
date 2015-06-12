package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

import io.vertx.ext.web.RoutingContext;

public class ParamsInjector implements AnnotatedParamInjector<Params> {

    private ParameterAdapterRegistry adapters;

    public ParamsInjector(ParameterAdapterRegistry adapters) {
        this.adapters = adapters;
    }

    @Override
    public Object resolve(RoutingContext context, Params annotation, Class<?> resultClass) throws BadRequestException {
        try {
            return adapters.adaptParams(context.request().params(), resultClass);
        } catch (Exception e) {
            throw new BadRequestException("At least one parameter is invalid", e);
        }
    }

}
