package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.ContextData;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import io.vertx.ext.web.RoutingContext;

public class ContextDataParamInjector implements AnnotatedParamInjector<ContextData> {

  @Override
  public Object resolve(RoutingContext context, ContextData annotation, String paramName, Class<?> resultClass) {
    return context.data();
  }

}
