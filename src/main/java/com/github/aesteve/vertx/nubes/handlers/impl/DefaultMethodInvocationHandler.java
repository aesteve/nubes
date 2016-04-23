package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;
import com.github.aesteve.vertx.nubes.handlers.AbstractMethodInvocationHandler;
import com.github.aesteve.vertx.nubes.marshallers.Payload;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class DefaultMethodInvocationHandler<T> extends AbstractMethodInvocationHandler<T> {

  private final static Logger LOG = LoggerFactory.getLogger(DefaultMethodInvocationHandler.class);

  public DefaultMethodInvocationHandler(Object instance, Method method, Config config, boolean hasNext, BiConsumer<RoutingContext, T> returnHandler) {
    super(instance, method, config, hasNext, returnHandler);
  }

  @Override
  public void handle(RoutingContext routingContext) {
    if (routingContext.response().ended()) {
      return;
    }
    if (routingContext.failed()) {
      return;
    }
    Object[] parameters;
    try {
      parameters = getParameters(routingContext);
    } catch (WrongParameterException e) {
      DefaultErrorHandler.badRequest(routingContext, e.getMessage());
      return;
    }
    try {
      @SuppressWarnings("unchecked")
      T returned = (T) method.invoke(instance, parameters);
      if (returnsSomething) {
        handleMethodReturn(routingContext, returned);
      }
      if (!usesRoutingContext && hasNext) { // cannot call context.next(), assume the method is sync
        routingContext.next();
      }
      if (!usesRoutingContext && !usesHttpResponse && !hasNext) {
        sendResponse(routingContext);
      }
    } catch (InvocationTargetException | IllegalAccessException ite) {
      LOG.error(ite);
      routingContext.fail(ite.getCause());
    }
  }

  private void sendResponse(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    try {
      response.setStatusCode(204);
      response.end();
    } catch (IllegalStateException ise) {
      // do not log for the user, this means the response has already been written
      // that'd mean something is wrong with the **framework** not users' code
      routingContext.next();
    }
  }

  private void handleMethodReturn(RoutingContext routingContext, T returned) {
    boolean contentTypeSet = routingContext.get(ContentTypeProcessor.BEST_CONTENT_TYPE) != null;
    if (returnHandler != null) {
      returnHandler.accept(routingContext, returned);
    } else if (hasNext && contentTypeSet) {
      // try to set as Payload
      Payload<Object> payload = routingContext.get(Payload.DATA_ATTR);
      if (payload == null) {
        payload = new Payload<>();
        routingContext.put(Payload.DATA_ATTR, payload);
      }
      payload.set(returned);
    } else if (returned instanceof String) {
      routingContext.response().end((String) returned);
    }
  }
}