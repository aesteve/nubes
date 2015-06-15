package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.context.ViewResolver;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.views.TemplateEngineManager;

public class ViewProcessor implements AnnotationProcessor<View> {

	private TemplateEngineManager templateHandler;
	private View annotation;

	public ViewProcessor(TemplateEngineManager templateHandler, View annotation) {
		this.templateHandler = templateHandler;
		this.annotation = annotation;
	}

	@Override
	public void preHandle(RoutingContext context) {
		String viewName = annotation.value();
		if (viewName != null) {
			ViewResolver.resolve(context, annotation.value());
		}
		context.next();
	}

	@Override
	public void postHandle(RoutingContext context) {
		templateHandler.handle(context);
	}

	@Override
	public Class<? extends View> getAnnotationType() {
		return View.class;
	}

}
