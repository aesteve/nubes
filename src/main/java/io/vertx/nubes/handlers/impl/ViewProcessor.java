package io.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.View;
import io.vertx.nubes.handlers.AnnotationProcessor;
import io.vertx.nubes.views.TemplateEngineManager;

public class ViewProcessor implements AnnotationProcessor<View> {

    private TemplateEngineManager templateHandler;
    private View annotation;

    public ViewProcessor(TemplateEngineManager templateHandler, View annotation) {
        this.templateHandler = templateHandler;
        this.annotation = annotation;
    }

    @Override
    public void preHandle(RoutingContext context) {
        System.out.println("preparing context for " + annotation.value());
        context.put("tplName", annotation.value());
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
