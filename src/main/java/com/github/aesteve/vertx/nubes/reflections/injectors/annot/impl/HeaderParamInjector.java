package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.Header;
import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

import io.vertx.ext.web.RoutingContext;

public class HeaderParamInjector implements AnnotatedParamInjector<Header> {

    private ParameterAdapterRegistry registry;

    public HeaderParamInjector(ParameterAdapterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object resolve(RoutingContext context, Header annotation, Class<?> resultClass) throws BadRequestException {
        String headerValue = context.request().getHeader(annotation.value());
        if (headerValue == null) {
            if (annotation.mandatory()) {
                throw new BadRequestException("Header : " + annotation.value() + " is mandatory");
            } else {
                return null;
            }
        }
        try {
            return registry.adaptParam(headerValue, resultClass);
        } catch (Exception e) {
            throw new BadRequestException("Header : " + annotation.value() + " is invalid", e);
        }
    }

}
