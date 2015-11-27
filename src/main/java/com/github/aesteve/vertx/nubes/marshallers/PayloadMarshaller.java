package com.github.aesteve.vertx.nubes.marshallers;

public interface PayloadMarshaller {

	public <T> T unmarshallPayload(String body, Class<T> clazz);

	public String marshallPayload(Object payload);

	public String marshallUnexpectedError(Throwable error, boolean displayDetails);

	public String marshallHttpStatus(int statusCode, String statusMessage);
}
