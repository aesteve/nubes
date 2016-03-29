package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

public class CheckAuthorityProcessor extends NoopAfterAllProcessor implements AnnotationProcessor<Auth> {

	private final Auth annotation;

	public CheckAuthorityProcessor(Auth annotation) {
		this.annotation = annotation;
	}

	@Override
	public void preHandle(RoutingContext context) {
		User user = context.user();
		if (user == null) {
			context.fail(401);
			return;
		}
		user.isAuthorised(annotation.authority(), result -> {
			if (!result.result()) {
				context.fail(403);
			} else {
				context.next();
			}
		});
	}

	@Override
	public void postHandle(RoutingContext context) {
		context.next();
	}
}
