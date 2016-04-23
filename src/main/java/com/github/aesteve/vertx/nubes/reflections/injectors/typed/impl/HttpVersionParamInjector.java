package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import io.vertx.core.http.HttpVersion;
import io.vertx.ext.web.RoutingContext;

public class HttpVersionParamInjector implements ParamInjector<HttpVersion> {

  @Override
  public HttpVersion resolve(RoutingContext context) {
    return context.request().version();
  }

}
