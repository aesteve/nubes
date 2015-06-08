package io.vertx.nubes.handlers.impl;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.handlers.Processor;
import io.vertx.nubes.marshallers.Payload;
import io.vertx.nubes.marshallers.PayloadMarshaller;

import java.util.Map;

public class PayloadTypeProcessor implements Processor {

    private Map<String, PayloadMarshaller> marshallers;

    public PayloadTypeProcessor(Map<String, PayloadMarshaller> marshallers) {
        this.marshallers = marshallers;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void preHandle(RoutingContext context) {
        Payload<?> payload = new Payload();
        context.put(Payload.DATA_ATTR, payload);
        context.next();
    }

    @Override
    public void postHandle(RoutingContext context) {
        Payload<?> payload = context.get(Payload.DATA_ATTR);
        HttpServerResponse response = context.response();
        if (response.ended()) {
            return;
        }
        Object userPayload = payload.get();
        if (userPayload == null) {
            response.setStatusCode(204);
            response.end();
        } else {
            PayloadMarshaller marshaller = marshallers.get(context.get("best-content-type"));
            response.setStatusCode(200);
            response.end(marshaller.marshallPayload(userPayload));
        }
    }

}
