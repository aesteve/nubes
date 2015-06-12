package com.github.aesteve.vertx.nubes.handlers;

import io.vertx.ext.web.RoutingContext;

public interface Processor {
    public void preHandle(RoutingContext context);

    public void postHandle(RoutingContext context);
}
