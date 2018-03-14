package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.context.ViewResolver;
import com.github.aesteve.vertx.nubes.exceptions.ValidationException;
import com.github.aesteve.vertx.nubes.exceptions.http.HttpException;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.utils.StackTracePrinter;
import com.github.aesteve.vertx.nubes.views.TemplateEngineManager;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class DefaultErrorHandler implements Handler<RoutingContext> {

  private static final String ERROR_DETAILS = "nubes-error-details";

  private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);

  private final Config config;
  private final Map<Integer, String> errorTemplates;
  private final Map<Integer, String> errorMessages;
  private final TemplateEngineManager templManager;
  private final Map<String, PayloadMarshaller> marshallers;

  public DefaultErrorHandler(Config config, TemplateEngineManager templManager, Map<String, PayloadMarshaller> marshallers) {
    this.config = config;
    this.templManager = templManager;
    this.marshallers = marshallers;
    errorTemplates = new HashMap<>();
    errorMessages = new HashMap<>();
    addDefaultErrorPages();
    addDefaultErrorMessages();
  }

  private static void setErrorDetails(RoutingContext context, String msg) {
    context.put(ERROR_DETAILS, msg);
  }

  public static void badRequest(RoutingContext context, String msg) {
    setErrorDetails(context, msg);
    context.fail(400);
  }

  @Override
  public void handle(RoutingContext context) {
    Throwable cause = context.failure();
    HttpServerResponse response = context.response();
    String contentType = ContentTypeProcessor.getContentType(context);
    PayloadMarshaller marshaller = marshallers.get(contentType);
    if (cause != null) {
      handleErrorWithCause(context, cause, response, contentType, marshaller);
    } else {
      handleHttpError(context, response, marshaller);
    }
  }

  private void handleHttpError(RoutingContext context, HttpServerResponse response, PayloadMarshaller marshaller) {
    final int status = context.statusCode();
    response.setStatusCode(status);
    String msg = errorMessages.getOrDefault(status, "Internal server error");
    if (context.get(ERROR_DETAILS) != null) {
      msg = context.get(ERROR_DETAILS);
    }
    if (marshaller != null) {
      response.end(marshaller.marshallHttpStatus(status, msg));
    } else {
      if (!response.ended()) {
        response.end(msg);
      }
    }
  }

  private void handleErrorWithCause(RoutingContext context, Throwable cause, HttpServerResponse response, String contentType, PayloadMarshaller marshaller) {
    int statusCode = 500;
    String statusMsg = errorMessages.get(500);
    if (cause instanceof HttpException) {
      HttpException he = (HttpException) cause;
      statusCode = he.status;
      statusMsg = he.getMessage();
    } else if (cause instanceof ValidationException) {
      ValidationException he = (ValidationException) cause;
      statusCode = 400;
      statusMsg = he.getValidationMsg();
    } else {
      LOG.error("Error caught by default error handler", cause);
    }
    response.setStatusCode(statusCode);
    if (isView(context)) {
      String tplFile = errorTemplates.get(statusCode);
      renderViewError(tplFile, context, cause);
    } else {
      if (marshaller == null) {
        if (!response.ended()) {
          response.end(statusMsg);
        }
        return;
      }
      response.putHeader(CONTENT_TYPE, contentType);
      if (statusCode == 500) {
        response.end(marshaller.marshallUnexpectedError(cause, config.isDisplayErrors()));
      } else {
        response.end(marshaller.marshallHttpStatus(statusCode, statusMsg));
      }
    }
  }

  private void addDefaultErrorPages() {
    errorTemplates.put(401, "web/views/errors/401.html");
    errorTemplates.put(403, "web/views/errors/403.html");
    errorTemplates.put(404, "web/views/errors/404.html");
    errorTemplates.put(420, "web/views/errors/420.html");
    errorTemplates.put(500, "web/views/errors/500.html");
  }

  private void addDefaultErrorMessages() {
    errorMessages.put(400, "Bad request");
    errorMessages.put(401, "Unauthorized");
    errorMessages.put(403, "Forbidden");
    errorMessages.put(404, "Not found");
    errorMessages.put(406, "Not acceptable");
    errorMessages.put(420, "Rate limitation exceeded");
    errorMessages.put(500, "Internal server error");
    errorMessages.put(503, "Service temporarily unavailable");
  }

  private static boolean isView(RoutingContext context) {
    return ViewResolver.getViewName(context) != null;
  }

  private void renderViewError(String tpl, RoutingContext context, Throwable cause) {
    HttpServerResponse response = context.response();
    if (tpl != null) {
      context.put("error", cause);
      if (tpl.endsWith(".html")) {
        response.sendFile(tpl);
        return;
      }
      if (config.isDisplayErrors()) {
        context.put("stackTrace", StackTracePrinter.asHtml(new StringBuilder(), cause).toString());
      }

      String fileName = Paths.get(tpl).getFileName().toString();
      String path = tpl.replace(fileName, "");

      templManager.fromViewName(tpl).render(context, fileName, path, res -> {
        if (res.succeeded()) {
          response.end(res.result());
        } else {
          LOG.error("Could not read error template : " + tpl, res.cause());
          response.end(errorMessages.get(500));
        }
      });
    } else {
      response.end(cause.getMessage());
    }
  }

}
