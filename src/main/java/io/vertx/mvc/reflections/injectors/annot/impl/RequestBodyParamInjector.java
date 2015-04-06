package io.vertx.mvc.reflections.injectors.annot.impl;

import java.util.Map;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.params.RequestBody;
import io.vertx.mvc.marshallers.PayloadMarshaller;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjector;

public class RequestBodyParamInjector implements AnnotatedParamInjector<RequestBody> {

	private static final Logger log = LoggerFactory.getLogger(RequestBodyParamInjector.class);
	
	private Map<String, PayloadMarshaller> marshallers;
	
	public RequestBodyParamInjector(Map<String, PayloadMarshaller> marshallers) {
		this.marshallers = marshallers;
	}
	
	@Override
	public Object resolve(RoutingContext context, RequestBody annotation, Class<?> resultClass) {
		String contentType = context.get("best-content-type");
		if (contentType == null) {
			log.error("No suitable Content-Type found, request body can't be read");
			return null;
		}
		PayloadMarshaller marshaller = marshallers.get(contentType);
		if (marshaller == null) {
			log.error("No marshaller found for Content-Type : "+contentType+", request body can't be read");
			return null;
		}
		return marshaller.unmarshallPayload(context.getBodyAsString(), resultClass);
	}

}
