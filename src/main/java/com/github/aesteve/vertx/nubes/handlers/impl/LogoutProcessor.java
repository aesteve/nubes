package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.auth.Logout;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

public class LogoutProcessor extends AfterProcessor implements AnnotationProcessor<Logout> {

	@Override
	public void postHandle(RoutingContext context) {
		User user = context.user();
		if (user != null) {
			user.clearCache();
			context.clearUser();
		}
		context.next();
	}

	@Override
	public Class<? extends Logout> getAnnotationType() {
		return Logout.class;
	}

}
