package io.vertx.mvc.handlers.impl;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.routing.ClientRedirect;
import io.vertx.mvc.handlers.AnnotationProcessor;

public class ClientRedirectProcessor implements AnnotationProcessor<ClientRedirect> {

    private final static String LOCATION_DATA = "client-redirect-location";

    @Override
    public void preHandle(RoutingContext context) {
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
    public void init(RoutingContext context, ClientRedirect annotation) {
        context.put(LOCATION_DATA, annotation.value());
    }

    @Override
    public Class<? extends ClientRedirect> getAnnotationType() {
        return ClientRedirect.class;
    }

}
