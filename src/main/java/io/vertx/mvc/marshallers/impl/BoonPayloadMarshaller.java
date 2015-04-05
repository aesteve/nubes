package io.vertx.mvc.marshallers.impl;

import org.boon.json.JsonFactory;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;

import io.vertx.mvc.marshallers.PayloadMarshaller;

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

}
