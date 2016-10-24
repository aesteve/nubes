package com.github.aesteve.vertx.nubes.marshallers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.utils.StackTracePrinter;
import io.vertx.core.VertxException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

import static com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller.Constants.ERROR_CODE_KEY;
import static com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller.Constants.ERROR_KEY;
import static com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller.Constants.ERROR_MESSAGE_KEY;

public class JacksonPayloadMarshaller implements PayloadMarshaller {

  protected final ObjectMapper mapper;

  public JacksonPayloadMarshaller() {
    this.mapper = new ObjectMapper();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unmarshallPayload(String body, Class<T> clazz) {
    if (clazz.equals(JsonObject.class)) {
      return (T) new JsonObject(body);
    } else if (clazz.equals(JsonArray.class)) {
      return (T) new JsonArray(body);
    }
    try {
      return mapper.readValue(body, clazz);
    } catch(IOException ioe) {
      throw new VertxException(ioe);
    }
  }

  @Override
  public String marshallPayload(Object payload) {
    if (payload instanceof JsonObject) {
      return payload.toString();
    } else if (payload instanceof JsonArray) {
      return payload.toString();
    }
    try {
      return mapper.writeValueAsString(payload);
    } catch(IOException ioe) {
      throw new VertxException(ioe);
    }
  }

  @Override
  public String marshallUnexpectedError(Throwable error, boolean displayDetails) {
    JsonObject json = new JsonObject();
    JsonObject jsonError = new JsonObject();
    json.put(ERROR_KEY, jsonError);
    jsonError.put(ERROR_CODE_KEY, 500);
    if (displayDetails) {
      jsonError.put(ERROR_MESSAGE_KEY, StackTracePrinter.asLineString(new StringBuilder(), error));
    } else {
      jsonError.put(ERROR_MESSAGE_KEY, "Internal Server Error");
    }
    return json.toString();
  }

  @Override
  public String marshallHttpStatus(int statusCode, String errorMessage) {
    JsonObject json = new JsonObject();
    JsonObject jsonError = new JsonObject();
    json.put(ERROR_KEY, jsonError);
    jsonError.put(ERROR_CODE_KEY, statusCode);
    jsonError.put(ERROR_MESSAGE_KEY, errorMessage);
    return json.toString();
  }

}
