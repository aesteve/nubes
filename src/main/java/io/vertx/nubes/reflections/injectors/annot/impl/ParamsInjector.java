package io.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.params.Params;
import io.vertx.nubes.exceptions.BadRequestException;
import io.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import io.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

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
