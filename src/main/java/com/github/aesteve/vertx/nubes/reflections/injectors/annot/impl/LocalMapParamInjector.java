package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.VertxLocalMap;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class LocalMapParamInjector implements AnnotatedParamInjector<VertxLocalMap> {

    @Override
    public Object resolve(RoutingContext context, VertxLocalMap annotation, Class<?> resultClass) {
        SharedData sd = context.vertx().sharedData();
        return sd.getLocalMap(annotation.value());
    }

}
