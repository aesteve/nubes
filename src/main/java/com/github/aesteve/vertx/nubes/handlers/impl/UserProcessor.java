package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.UserSessionHandler;

import com.github.aesteve.vertx.nubes.handlers.Processor;

public class UserProcessor implements Processor {

	private UserSessionHandler handler;

	public UserProcessor(AuthProvider provider) {
		handler = UserSessionHandler.create(provider);
	}

	@Override
	public void preHandle(RoutingContext context) {
		handler.handle(context);
	}

	@Override
	public void postHandle(RoutingContext context) {
		context.next();
	}

}
