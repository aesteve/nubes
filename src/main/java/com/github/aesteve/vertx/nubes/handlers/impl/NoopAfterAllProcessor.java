package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.handlers.Processor;

public abstract class NoopAfterAllProcessor implements Processor {

	public void afterAll(RoutingContext context) {
		context.next();
	}

}
