package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.context.ViewResolver;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.utils.StackTracePrinter;
import com.github.aesteve.vertx.nubes.views.TemplateEngineManager;

public class DefaultErrorHandler implements Handler<RoutingContext> {

	public final static String ERROR_DETAILS = "nubes-error-details";

	private final static Logger log = LoggerFactory.getLogger(DefaultErrorHandler.class);

	private Config config;
	private Map<Integer, String> errorTemplates;
	private Map<Integer, String> errorMessages;
	private TemplateEngineManager templManager;
	private Map<String, PayloadMarshaller> marshallers;

	public DefaultErrorHandler(Config config, TemplateEngineManager templManager, Map<String, PayloadMarshaller> marshallers) {
		this.config = config;
		this.templManager = templManager;
		this.marshallers = marshallers;
		errorTemplates = new HashMap<Integer, String>();
		errorMessages = new HashMap<Integer, String>();
		addDefaultErrorPages();
		addDefaultErrorMessages();
	}

	public static void setErrorDetails(RoutingContext context, String msg) {
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
		if (cause != null) {
			log.error("Error caught by default error handler", cause);
		}
		PayloadMarshaller marshaller = marshallers.get(ContentTypeProcessor.getContentType(context));
		if (cause != null) {
			response.setStatusCode(500);
			if (isView(context)) {
				String tplFile = errorTemplates.get(500);
				renderViewError(tplFile, context, cause);
			} else {
				if (marshaller != null) {
					response.end(marshaller.marshallUnexpectedError(cause, config.displayErrors));
				} else {
					response.end(errorMessages.get(500));
				}
			}
		} else {
			int status = context.statusCode();
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
	}

	protected void addDefaultErrorPages() {
		errorTemplates.put(401, "web/views/errors/401.html");
		errorTemplates.put(403, "web/views/errors/403.html");
		errorTemplates.put(404, "web/views/errors/404.html");
		errorTemplates.put(420, "web/views/errors/420.html");
		errorTemplates.put(500, "web/views/errors/500.html");
	}

	protected void addDefaultErrorMessages() {
		errorMessages.put(400, "Bad request");
		errorMessages.put(401, "Unauthorized");
		errorMessages.put(403, "Forbidden");
		errorMessages.put(404, "Not found");
		errorMessages.put(406, "Not acceptable");
		errorMessages.put(420, "Rate limitation exceeded");
		errorMessages.put(500, "Internal server error");
		errorMessages.put(503, "Service temporarily unavailable");
	}

	private boolean isView(RoutingContext context) {
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
			if (config.displayErrors) {
				context.put("stackTrace", StackTracePrinter.asHtml(null, cause).toString());
			}
			templManager.fromViewName(tpl).render(context, tpl, res -> {
				if (res.succeeded()) {
					response.end(res.result());
				} else {
					log.error("Could not read error template : " + tpl, res.cause());
					response.end(errorMessages.get(500));
				}
			});
		} else {
			response.end(cause.getMessage());
		}
	}

}
