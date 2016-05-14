package com.github.aesteve.vertx.nubes.context;

import io.vertx.ext.web.RoutingContext;

public interface ViewResolver {

  String CONTEXT_TPL_NAME = "tpl-name";

  static void resolve(RoutingContext context, String viewName) {
    context.put(CONTEXT_TPL_NAME, viewName);
  }

  static String getViewName(RoutingContext context) {
    return context.get(CONTEXT_TPL_NAME);
  }
}
