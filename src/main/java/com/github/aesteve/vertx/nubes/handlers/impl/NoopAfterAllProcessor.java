package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.handlers.Processor;
import io.vertx.ext.web.RoutingContext;

public abstract class NoopAfterAllProcessor implements Processor {

  @Override
  public void afterAll(RoutingContext context) {
    context.next();
  }

}
