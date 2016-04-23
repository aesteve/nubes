package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.handlers.Processor;
import io.vertx.ext.web.RoutingContext;

public abstract class AfterProcessor extends NoopAfterAllProcessor implements Processor {

  public void preHandle(RoutingContext context) {
    context.next();
  }

}
