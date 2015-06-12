package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

import io.vertx.ext.web.RoutingContext;

public class ParamInjector implements AnnotatedParamInjector<Param> {

    private ParameterAdapterRegistry adapters;

    public ParamInjector(ParameterAdapterRegistry adapters) {
        this.adapters = adapters;
    }

    @Override
    public Object resolve(RoutingContext context, Param annotation, Class<?> resultClass) throws BadRequestException {
        String paramValue = context.request().getParam(annotation.value());
        if (paramValue == null) {
            if (annotation.mandatory()) {
                throw new BadRequestException("Parameter " + annotation.value() + " is mandatory");
            } else {
                return null;
            }
        }
        try {
            return adapters.adaptParam(paramValue, resultClass);
        } catch (Exception e) {
            throw new BadRequestException("Param " + annotation.value() + " is invalid", e);
        }
    }

}
