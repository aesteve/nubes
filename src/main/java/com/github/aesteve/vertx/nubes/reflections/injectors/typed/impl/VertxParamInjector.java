package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class VertxParamInjector implements ParamInjector<Vertx> {

  @Override
  public Vertx resolve(RoutingContext context) {
    return context.vertx();
  }

}
