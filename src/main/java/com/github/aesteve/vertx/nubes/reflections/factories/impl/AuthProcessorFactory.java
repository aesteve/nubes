package com.github.aesteve.vertx.nubes.reflections.factories.impl;

import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.CheckAuthorityProcessor;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class AuthProcessorFactory implements AnnotationProcessorFactory<Auth> {

	@Override
	public AnnotationProcessor<Auth> create(Auth annotation) {
		return new CheckAuthorityProcessor(annotation);
	}

}
