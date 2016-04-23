package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class ResponseParamInjector implements ParamInjector<HttpServerResponse> {

  @Override
  public HttpServerResponse resolve(RoutingContext context) {
    return context.response();
  }

}
