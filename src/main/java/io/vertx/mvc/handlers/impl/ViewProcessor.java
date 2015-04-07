package io.vertx.mvc.handlers.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.View;
import io.vertx.mvc.handlers.AnnotationProcessor;
import io.vertx.mvc.views.TemplateEngineManager;

public class ViewProcessor implements AnnotationProcessor<View> {

	private TemplateEngineManager templateHandler;
	
	public ViewProcessor(TemplateEngineManager templateHandler) {
		this.templateHandler = templateHandler;
	}
	
	@Override
	public void preHandle(RoutingContext context) {
		context.next();
	}

	@Override
	public void postHandle(RoutingContext context) {
		templateHandler.handle(context);
	}

	@Override
	public void init(RoutingContext context, View annotation) {
		context.put("tplName", annotation.value());
	}

	@Override
	public Class<? extends View> getAnnotationType() {
		return View.class;
	}

}
