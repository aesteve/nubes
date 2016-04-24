package com.github.aesteve.vertx.nubes.marshallers;

public interface PayloadMarshaller {

  <T> T unmarshallPayload(String body, Class<T> clazz);

  String marshallPayload(Object payload);

  String marshallUnexpectedError(Throwable error, boolean displayDetails);

  String marshallHttpStatus(int statusCode, String statusMessage);

  final class Constants {
    public static final String ERROR_KEY = "error";
    public static final String ERROR_CODE_KEY = "code";
    public static final String ERROR_MESSAGE_KEY = "message";
    private Constants() {}
  }

}
