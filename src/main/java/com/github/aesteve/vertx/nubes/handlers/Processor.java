package com.github.aesteve.vertx.nubes.handlers;

import io.vertx.ext.web.RoutingContext;

public interface Processor {
	void preHandle(RoutingContext context);

	void postHandle(RoutingContext context);

	void afterAll(RoutingContext context);
}
