package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.handlers.impl.ContentTypeProcessor;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

public class RequestBodyParamInjector implements AnnotatedParamInjector<RequestBody> {

  private static final Logger LOG = LoggerFactory.getLogger(RequestBodyParamInjector.class);

  private final Map<String, PayloadMarshaller> marshallers;

  public RequestBodyParamInjector(Map<String, PayloadMarshaller> marshallers) {
    this.marshallers = marshallers;
  }

  @Override
  public Object resolve(RoutingContext context, RequestBody annotation, String paramName, Class<?> resultClass) {
    String body = context.getBodyAsString();
    if (resultClass.equals(String.class)) {
      return body;
    }
    String contentType = ContentTypeProcessor.getContentType(context);
    if (contentType == null) {
      LOG.error("No suitable Content-Type found, request body can't be read");
      return null;
    }
    PayloadMarshaller marshaller = marshallers.get(contentType);
    if (marshaller == null) {
      LOG.error("No marshaller found for Content-Type : " + contentType + ", request body can't be read");
      return null;
    }
    try {
      return marshaller.unmarshallPayload(body, resultClass);
    } catch (Exception e) { //NOSONAR
      // not logged, since it could lead to vulnerabilities (generating huge logs + overhead simply by sending bad payloads)
      context.fail(400);
      return null;
    }
  }

}
