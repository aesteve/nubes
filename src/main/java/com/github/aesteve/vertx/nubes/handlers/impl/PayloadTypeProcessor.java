package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.marshallers.Payload;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;

public class PayloadTypeProcessor extends NoopAfterAllProcessor implements Processor {

	private Map<String, PayloadMarshaller> marshallers;

	public PayloadTypeProcessor(Map<String, PayloadMarshaller> marshallers) {
		this.marshallers = marshallers;
	}

	@Override
	public void preHandle(RoutingContext context) {
		Payload<?> payload = new Payload<>();
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
			String contentType = ContentTypeProcessor.getContentType(context);
			if (contentType == null) {
				context.fail(new IllegalArgumentException("No content-type defined, cannot marshall payload"));
				return;
			}
			PayloadMarshaller marshaller = marshallers.get(contentType);
			if (marshaller == null) {
				context.fail(new IllegalArgumentException("No marshaller found for content-type : " + contentType));
				return;
			}
			String marshalled = marshaller.marshallPayload(userPayload);
			response.setStatusCode(200);
			response.end(marshalled);
		}
	}

}
