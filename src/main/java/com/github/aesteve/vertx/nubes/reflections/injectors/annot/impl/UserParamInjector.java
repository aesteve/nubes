package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.auth.User;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import io.vertx.ext.web.RoutingContext;

public class UserParamInjector implements AnnotatedParamInjector<User> {

  @Override
  public Object resolve(RoutingContext context, User annotation, String paramName, Class<?> resultClass) {
    return context.user();
  }

}
