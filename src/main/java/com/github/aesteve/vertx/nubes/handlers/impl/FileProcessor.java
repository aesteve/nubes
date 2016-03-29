package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.context.FileResolver;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

public class FileProcessor extends NoopAfterAllProcessor implements AnnotationProcessor<File> {

	private final File annotation;

	public FileProcessor(File annotation) {
		this.annotation = annotation;
	}

	@Override
	public void preHandle(RoutingContext context) {
		String fileName = annotation.value();
		if (fileName != null) {
			FileResolver.resolve(context, annotation.value());
		}
		context.next();
	}

	@Override
	public void postHandle(RoutingContext context) {
		context.response().sendFile(FileResolver.getFileName(context));
	}

}
