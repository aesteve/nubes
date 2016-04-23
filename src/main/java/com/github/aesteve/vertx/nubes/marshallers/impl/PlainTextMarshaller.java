package com.github.aesteve.vertx.nubes.marshallers.impl;

import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.utils.StackTracePrinter;
import io.vertx.core.VertxException;

public class PlainTextMarshaller implements PayloadMarshaller {

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unmarshallPayload(String body, Class<T> clazz) {
		if (!String.class.isAssignableFrom(clazz)) {
			throw new VertxException("text/plain should only be used to marshall Strings");
		}
		return (T) body;
	}

	@Override
	public String marshallPayload(Object payload) {
		if (payload instanceof String) {
			return payload.toString();
		} else {
			throw new VertxException("text/plain should only be used to marshall Strings");
		}
	}

	@Override
	public String marshallUnexpectedError(Throwable error, boolean displayDetails) {
		if (displayDetails) {
			return StackTracePrinter.asLineString(null, error).toString();
		} else {
			return "Internal server error";
		}
	}

	@Override
	public String marshallHttpStatus(int statusCode, String errorMessage) {
		return statusCode + " : " + errorMessage;
	}

}
