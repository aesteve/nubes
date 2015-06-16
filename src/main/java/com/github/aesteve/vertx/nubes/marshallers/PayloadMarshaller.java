package com.github.aesteve.vertx.nubes.marshallers;

import com.github.aesteve.vertx.nubes.exceptions.MarshallingException;

public interface PayloadMarshaller {

    public <T> T unmarshallPayload(String body, Class<T> clazz) throws MarshallingException;

    public String marshallPayload(Object payload) throws MarshallingException;

    public String marshallUnexpectedError(Throwable error, boolean displayDetails);

    public String marshallHttpStatus(int statusCode, String statusMessage);
}
