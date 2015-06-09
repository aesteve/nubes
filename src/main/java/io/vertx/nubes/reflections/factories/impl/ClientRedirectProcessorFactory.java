package io.vertx.nubes.reflections.factories.impl;

import io.vertx.nubes.annotations.routing.ClientRedirect;
import io.vertx.nubes.handlers.AnnotationProcessor;
import io.vertx.nubes.handlers.impl.ClientRedirectProcessor;
import io.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class ClientRedirectProcessorFactory implements AnnotationProcessorFactory<ClientRedirect> {

    @Override
    public AnnotationProcessor<ClientRedirect> create(ClientRedirect annotation) {
        return new ClientRedirectProcessor(annotation);
    }

}
