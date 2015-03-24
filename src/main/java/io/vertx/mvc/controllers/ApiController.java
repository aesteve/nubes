package io.vertx.mvc.controllers;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.AfterFilter;
import io.vertx.mvc.annotations.BeforeFilter;
import io.vertx.mvc.annotations.Finalizer;

import java.util.List;

abstract public class ApiController {
	abstract protected List<String> contentTypes();
	abstract protected String marshallPayload(Object payload);
	
	protected String matchingContentType(RoutingContext context) {
		HttpServerRequest request = context.request();
		String accept = request.getHeader("Accept");
		if (accept == null) {
			return null;
		}
		return contentTypes().stream().filter(contentType -> {
			return accept.toLowerCase().indexOf(contentType.toLowerCase()) > -1;
		}).findFirst().orElse(null);
	}
	
	@BeforeFilter
	public void setContentType(RoutingContext context) {
		HttpServerResponse response = context.response();
		String matchingContentType = matchingContentType(context);
		if (matchingContentType == null) {
			response.setStatusCode(406);
			response.end("Not acceptable");
			return;
		}
		response.headers().add("Content-Type", matchingContentType);
		context.next();
	}
	
	@AfterFilter
	public void calculateETag(RoutingContext context) {
		// TODO
		context.next();
	}
	
	@AfterFilter
	public void setPaginationHeaders(RoutingContext context) {
		// TODO
		context.next();
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