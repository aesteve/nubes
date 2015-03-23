package io.vertx.mvc.controllers;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;

public abstract class AbstractController {

	protected void render(RoutingContext context, String text) {
		context.response().end(text);
	}
	
	protected void render(RoutingContext context, String text, String contentType) {
		HttpServerResponse response = context.response();
		response.headers().add("Content-Type", contentType);
		response.end(text);
	}
}
