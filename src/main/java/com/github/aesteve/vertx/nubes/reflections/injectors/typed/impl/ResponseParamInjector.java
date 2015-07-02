package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

public class ResponseParamInjector implements ParamInjector<HttpServerResponse> {

	@Override
	public HttpServerResponse resolve(RoutingContext context) {
		return context.response();
	}

}
