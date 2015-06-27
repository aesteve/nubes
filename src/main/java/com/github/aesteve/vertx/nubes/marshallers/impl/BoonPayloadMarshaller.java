package com.github.aesteve.vertx.nubes.marshallers.impl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.boon.json.JsonFactory;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;

import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.utils.StackTracePrinter;

public class BoonPayloadMarshaller implements PayloadMarshaller {

	protected JsonSerializer serializer;
	protected ObjectMapper mapper;

	public BoonPayloadMarshaller() {
		this.serializer = new JsonSerializerFactory().useAnnotations().create();
		this.mapper = JsonFactory.create();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unmarshallPayload(String body, Class<T> clazz) {
		if (clazz.equals(JsonObject.class)) {
			return (T) new JsonObject(body);
		} else if (clazz.equals(JsonArray.class)) {
			return (T) new JsonArray(body);
		}
		return mapper.fromJson(body, clazz);
	}

	@Override
	public String marshallPayload(Object payload) {
		if (payload instanceof JsonObject) {
			return payload.toString();
		} else if (payload instanceof JsonArray) {
			return payload.toString();
		}
		return serializer.serialize(payload).toString();
	}

	@Override
	public String marshallUnexpectedError(Throwable error, boolean displayDetails) {
		JsonObject json = new JsonObject();
		JsonObject jsonError = new JsonObject();
		json.put("error", jsonError);
		jsonError.put("code", 500);
		if (displayDetails) {
			jsonError.put("message", StackTracePrinter.asLineString(null, error));
		} else {
			jsonError.put("message", "Internal Server Error");
		}
		return json.toString();
	}

	@Override
	public String marshallHttpStatus(int statusCode, String errorMessage) {
		JsonObject json = new JsonObject();
		JsonObject jsonError = new JsonObject();
		json.put("error", jsonError);
		jsonError.put("code", statusCode);
		jsonError.put("message", errorMessage);
		return json.toString();
	}

}
