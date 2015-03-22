package io.vertx.mvc.controllers;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.AfterFilter;
import io.vertx.mvc.annotations.Finalizer;

abstract public class ApiController {
	abstract protected String contentType();
	abstract protected String marshallPayload(Object payload);
	
	@AfterFilter
	public void setContentType(RoutingContext context) {
		context.response().headers().add("Content-Type", contentType());
	}
	
	@AfterFilter
	public void calculateETag(RoutingContext context) {
		// TODO
	}
	
	@AfterFilter
	public void setPaginationHeaders() {
		// TODO
	}
	
	@Finalizer
	public void sendResponse(RoutingContext context) {
		HttpServerResponse response = context.response();
		Object payload = context.data().get("payload"); 
		if (payload != null) {
			response.setStatusCode(200);
			response.end(marshallPayload(payload));
		} else {
			response.setStatusCode(204);
			response.end();
		}
	}
}
