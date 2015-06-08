package io.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.reflections.injectors.typed.ParamInjector;

public class RoutingContextParamInjector implements ParamInjector<RoutingContext> {

    @Override
    public RoutingContext resolve(RoutingContext context) {
        return context;
    }

}
