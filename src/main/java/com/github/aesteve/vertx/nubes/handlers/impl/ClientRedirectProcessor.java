package com.github.aesteve.vertx.nubes.handlers.impl;

import static io.vertx.core.http.HttpHeaders.LOCATION;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.routing.Redirect;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

public class ClientRedirectProcessor extends NoopAfterAllProcessor implements AnnotationProcessor<Redirect> {

	private final static String LOCATION_DATA = "client-redirect-location";

	private Redirect annotation;

	public ClientRedirectProcessor(Redirect annotation) {
		this.annotation = annotation;
	}

	@Override
	public void preHandle(RoutingContext context) {
		context.put(LOCATION_DATA, annotation.value());
		context.next();
	}

	@Override
	public void postHandle(RoutingContext context) {
		HttpServerResponse response = context.response();
		String location = context.get(LOCATION_DATA);
		response.setStatusCode(annotation.code());
		response.putHeader(LOCATION, location);
		response.end();
	}

	@Override
	public Class<? extends Redirect> getAnnotationType() {
		return Redirect.class;
	}

}
