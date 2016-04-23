package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

public class RequestParamInjector implements ParamInjector<HttpServerRequest> {

  @Override
  public HttpServerRequest resolve(RoutingContext context) {
    return context.request();
  }

}
