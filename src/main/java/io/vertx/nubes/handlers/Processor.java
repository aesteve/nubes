package io.vertx.nubes.handlers;

import io.vertx.ext.apex.RoutingContext;

public interface Processor {
	public void preHandle(RoutingContext context);
	public void postHandle(RoutingContext context);
}
