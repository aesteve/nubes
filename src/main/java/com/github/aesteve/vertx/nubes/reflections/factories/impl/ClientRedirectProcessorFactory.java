package com.github.aesteve.vertx.nubes.reflections.factories.impl;

import com.github.aesteve.vertx.nubes.annotations.routing.ClientRedirect;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.ClientRedirectProcessor;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class ClientRedirectProcessorFactory implements AnnotationProcessorFactory<ClientRedirect> {

	@Override
	public AnnotationProcessor<ClientRedirect> create(ClientRedirect annotation) {
		return new ClientRedirectProcessor(annotation);
	}

}
