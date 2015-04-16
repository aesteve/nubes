package io.vertx.mvc.handlers.impl;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.Config;
import io.vertx.mvc.exceptions.HttpException;
import io.vertx.mvc.marshallers.PayloadMarshaller;
import io.vertx.mvc.utils.StackTracePrinter;
import io.vertx.mvc.views.TemplateEngineManager;

import java.util.HashMap;
import java.util.Map;

public class DefaultErrorHandler implements Handler<RoutingContext> {

    private final static Logger log = LoggerFactory.getLogger(DefaultErrorHandler.class);
    private final static String DEFAULT_ERROR_MSG = "Internal server error";

    private Config config;
    private Map<Integer, String> errorTemplates;
    private TemplateEngineManager templManager;
    private Map<String, PayloadMarshaller> marshallers;

    public DefaultErrorHandler(Config config, TemplateEngineManager templManager, Map<String, PayloadMarshaller> marshallers) {
        this.config = config;
        this.templManager = templManager;
        this.marshallers = marshallers;
        errorTemplates = new HashMap<Integer, String>();
        addDefaultErrorPages();
    }

    @Override
    public void handle(RoutingContext context) {
        Throwable cause = context.failure();
        HttpServerResponse response = context.response();
        log.error("Error in routing context caught by defaultErrorHandler", cause);
        if (cause instanceof HttpException) {
            HttpException he = (HttpException) cause;
            response.setStatusCode(he.getStatusCode());
            response.setStatusMessage(he.getMessage());
            if (isView(context)) {
                String tpl = errorTemplates.get(he.getStatusCode());
                renderViewError(tpl, context, he);
            } else {
                PayloadMarshaller marshaller = marshallers.get(context.get("best-content-type"));
                if (marshaller != null) {
                    marshaller.marshallHttpError(he, config.displayErrors);
                } else {
                    response.end(he.getMessage());
                }
            }
        } else if (cause != null) {
            response.setStatusCode(500);
            if (isView(context)) {
                String tplFile = errorTemplates.get(500);
                renderViewError(tplFile, context, cause);
            } else {
                PayloadMarshaller marshaller = marshallers.get(context.get("best-content-type"));
                if (marshaller != null) {
                    marshaller.marshallUnexpectedError(cause, config.displayErrors);
                }
            }
        } else {
            response.setStatusCode(context.statusCode());
            response.end(DEFAULT_ERROR_MSG);
        }
    }

    protected void addDefaultErrorPages() {
        errorTemplates.put(401, "errors/401.html");
        errorTemplates.put(403, "errors/403.html");
        errorTemplates.put(404, "errors/404.html");
        errorTemplates.put(420, "errors/420.html");
        errorTemplates.put(500, "errors/500.html");
    }

    private boolean isView(RoutingContext context) {
        return context.get("tplName") != null;
    }

    private void renderViewError(String tpl, RoutingContext context, Throwable cause) {
        HttpServerResponse response = context.response();
        if (tpl != null) {
            context.put("error", cause);
            if (config.displayErrors) {
                context.put("stackTrace", StackTracePrinter.asHtml(null, cause).toString());
            }
            templManager.fromViewName(tpl).render(context, tpl, res -> {
                if (res.succeeded()) {
                    response.end(res.result());
                } else {
                    log.error("Could not read error template : " + tpl, res.cause());
                    response.end(DEFAULT_ERROR_MSG);
                }
            });
        } else {
            response.end(cause.getMessage());
        }
    }

}
