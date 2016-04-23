package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.annotations.auth.Logout;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

public class LogoutProcessor extends AfterProcessor implements AnnotationProcessor<Logout> {

  @Override
  public void postHandle(RoutingContext context) {
    User user = context.user();
    if (user != null) {
      user.clearCache();
      context.clearUser();
    }
    context.next();
  }

}
