package io.vertx.mvc.marshallers.impl;

import org.boon.json.JsonFactory;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;

import io.vertx.core.json.JsonObject;
import io.vertx.mvc.exceptions.HttpException;
import io.vertx.mvc.marshallers.PayloadMarshaller;
import io.vertx.mvc.utils.StackTracePrinter;

public class BoonPayloadMarshaller implements PayloadMarshaller {

    protected JsonSerializer serializer;
    protected ObjectMapper mapper;

	
	public BoonPayloadMarshaller() {
		this.serializer = new JsonSerializerFactory().useAnnotations().create();
		this.mapper = JsonFactory.create();
	}
	
	@Override
	public<T> T unmarshallPayload(String body, Class<T> clazz) {
		return mapper.fromJson(body, clazz);
	}

	@Override
	public String marshallPayload(Object payload) {
		return serializer.serialize(payload).toString();
	}

	@Override
	public String marshallUnexpectedError(Throwable error, boolean displayDetails) {
		JsonObject json = new JsonObject();
		json.put("code", 500);
		if (displayDetails) {
			json.put("message", StackTracePrinter.asLineString(null, error));
		} else {
			json.put("message", "Internal Server Error");
		}
		return json.toString();
	}

	@Override
	public String marshallHttpError(HttpException error, boolean displayDetails) {
		JsonObject json = new JsonObject();
		json.put("code", error.getStatusCode());
		json.put("message", error.getStatusMessage());
		return json.toString();
	}

}
