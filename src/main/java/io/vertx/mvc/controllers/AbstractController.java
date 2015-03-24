package io.vertx.mvc.controllers;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.context.PaginationContext;

public abstract class AbstractController {

	protected void render(RoutingContext context, String text) {
		context.response().end(text);
	}
	
	protected void render(RoutingContext context, String text, String contentType) {
		HttpServerResponse response = context.response();
		response.headers().add("Content-Type", contentType);
		response.end(text);
	}
	
	protected PaginationContext getPaginationContext(RoutingContext context) {
		PaginationContext pageContext = (PaginationContext)context.data().get(PaginationContext.DATA_ATTR);
		if (pageContext == null) {
			throw new IllegalStateException("In order to retrieve pagination context you have to annotate your method with @Paginated");
		}
		return pageContext;
	}
}
