package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.auth.User;
import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class UserParamInjector implements AnnotatedParamInjector<User> {

	@Override
	public Object resolve(RoutingContext context, User annotation, Class<?> resultClass) throws BadRequestException {
		return context.user();
	}

}
