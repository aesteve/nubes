package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

import io.vertx.ext.web.RoutingContext;

public class FileProcessor implements AnnotationProcessor<File> {

    private File annotation;

    public FileProcessor(File annotation) {
        this.annotation = annotation;
    }

    @Override
    public void preHandle(RoutingContext context) {
        context.next();
    }

    @Override
    public void postHandle(RoutingContext context) {
        context.response().sendFile(annotation.value());
    }

    @Override
    public Class<? extends File> getAnnotationType() {
        return File.class;
    }

}
