package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class ParamInjector implements AnnotatedParamInjector<Param> {

    private ParameterAdapterRegistry adapters;

    public ParamInjector(ParameterAdapterRegistry adapters) {
        this.adapters = adapters;
    }

    @Override
    public Object resolve(RoutingContext context, Param annotation, Class<?> resultClass) {
        String paramValue = context.request().getParam(annotation.value());
        if (paramValue == null) {
            if (annotation.mandatory()) {
                DefaultErrorHandler.badRequest(context, "Parameter " + annotation.value() + " is mandatory");
            }
            return null;
        }
        try {
            return adapters.adaptParam(paramValue, resultClass);
        } catch (Exception e) {
            DefaultErrorHandler.badRequest(context, "Wrong value for param : " + annotation.value());
            return null;
        }
    }

}
