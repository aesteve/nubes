package io.vertx.nubes.marshallers;

import io.vertx.nubes.exceptions.HttpException;

public interface PayloadMarshaller {
	public<T> T unmarshallPayload(String body, Class<T> clazz);
	public String marshallPayload(Object payload);
	public String marshallUnexpectedError(Throwable error, boolean displayDetails);
	public String marshallHttpError(HttpException error, boolean displayDetails);
}
