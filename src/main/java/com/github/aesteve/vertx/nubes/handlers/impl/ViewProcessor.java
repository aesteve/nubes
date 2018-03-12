package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.context.ViewResolver;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.views.TemplateEngineManager;
import io.vertx.ext.web.RoutingContext;

public class ViewProcessor extends NoopAfterAllProcessor implements AnnotationProcessor<View> {

  private final TemplateEngineManager templateHandler;
  private final View annotation;

  public ViewProcessor(TemplateEngineManager templateHandler, View annotation) {
    this.templateHandler = templateHandler;
    this.annotation = annotation;
  }

  @Override
  public void preHandle(RoutingContext context) {
    String viewName = annotation.value();
    ViewResolver.resolve(context, annotation.value());
    context.next();
  }

  @Override
  public void postHandle(RoutingContext context) {
    templateHandler.handle(context);
  }

}
