package io.vertx.mvc.marshallers;

import io.vertx.mvc.exceptions.HttpException;

public interface PayloadMarshaller {
	public<T> T unmarshallPayload(String body, Class<T> clazz);
	public String marshallPayload(Object payload);
	public String marshallUnexpectedError(Throwable error, boolean displayDetails);
	public String marshallHttpError(HttpException error, boolean displayDetails);
}
