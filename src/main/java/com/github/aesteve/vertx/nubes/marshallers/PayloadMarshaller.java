package com.github.aesteve.vertx.nubes.marshallers;

public interface PayloadMarshaller {

  <T> T unmarshallPayload(String body, Class<T> clazz);

  String marshallPayload(Object payload);

  String marshallUnexpectedError(Throwable error, boolean displayDetails);

  String marshallHttpStatus(int statusCode, String statusMessage);
}
