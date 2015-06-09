package io.vertx.nubes.reflections.factories.impl;

import io.vertx.nubes.annotations.mixins.ContentType;
import io.vertx.nubes.handlers.AnnotationProcessor;
import io.vertx.nubes.handlers.impl.ContentTypeProcessor;
import io.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class ContentTypeProcessorFactory implements AnnotationProcessorFactory<ContentType> {

    @Override
    public AnnotationProcessor<ContentType> create(ContentType annotation) {
        return new ContentTypeProcessor(annotation);
    }

}
