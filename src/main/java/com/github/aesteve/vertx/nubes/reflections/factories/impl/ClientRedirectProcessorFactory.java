package com.github.aesteve.vertx.nubes.reflections.factories.impl;

import com.github.aesteve.vertx.nubes.annotations.routing.Redirect;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.ClientRedirectProcessor;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class ClientRedirectProcessorFactory implements AnnotationProcessorFactory<Redirect> {

  @Override
  public AnnotationProcessor<Redirect> create(Redirect annotation) {
    return new ClientRedirectProcessor(annotation);
  }

}
