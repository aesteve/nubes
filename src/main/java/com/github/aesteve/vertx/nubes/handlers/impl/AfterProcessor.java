package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.handlers.Processor;

public abstract class AfterProcessor extends NoopAfterAllProcessor implements Processor {

	public void preHandle(RoutingContext context) {
		context.next();
	}

}
