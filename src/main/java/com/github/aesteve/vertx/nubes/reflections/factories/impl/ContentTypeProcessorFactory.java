package com.github.aesteve.vertx.nubes.reflections.factories.impl;

import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.ContentTypeProcessor;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class ContentTypeProcessorFactory implements AnnotationProcessorFactory<ContentType> {

    @Override
    public AnnotationProcessor<ContentType> create(ContentType annotation) {
        return new ContentTypeProcessor(annotation);
    }

}
