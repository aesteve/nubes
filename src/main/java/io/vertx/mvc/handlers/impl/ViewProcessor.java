package io.vertx.mvc.handlers.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.View;
import io.vertx.mvc.handlers.AnnotationProcessor;
import io.vertx.mvc.views.TemplateEngineManager;

public class ViewProcessor implements AnnotationProcessor<View> {

	private String viewName;
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
		context.put("tplName", viewName);
		templateHandler.handle(context);
	}

	@Override
	public void init(View annotation) {
		this.viewName = annotation.value();
	}

}
