package io.vertx.mvc.marshallers;

public interface PayloadMarshaller {
	public<T> T unmarshallPayload(String body, Class<T> clazz);
	public String marshallPayload(Object payload);
}
