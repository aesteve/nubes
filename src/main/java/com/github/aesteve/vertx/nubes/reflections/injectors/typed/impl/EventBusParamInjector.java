package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

public class EventBusParamInjector implements ParamInjector<EventBus> {

    @Override
    public EventBus resolve(RoutingContext context) {
        return context.vertx().eventBus();
    }

}
