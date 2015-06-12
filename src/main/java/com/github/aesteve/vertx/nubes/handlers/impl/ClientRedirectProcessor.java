package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.annotations.routing.ClientRedirect;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class ClientRedirectProcessor implements AnnotationProcessor<ClientRedirect> {

    private final static String LOCATION_DATA = "client-redirect-location";

    private ClientRedirect annotation;

    public ClientRedirectProcessor(ClientRedirect annotation) {
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
        response.putHeader("Location", location);
        response.end();
    }

    @Override
    public Class<? extends ClientRedirect> getAnnotationType() {
        return ClientRedirect.class;
    }

}
