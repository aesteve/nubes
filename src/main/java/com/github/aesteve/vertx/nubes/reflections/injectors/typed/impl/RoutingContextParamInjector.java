package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

import io.vertx.ext.web.RoutingContext;

public class RoutingContextParamInjector implements ParamInjector<RoutingContext> {

    @Override
    public RoutingContext resolve(RoutingContext context) {
        return context;
    }

}
