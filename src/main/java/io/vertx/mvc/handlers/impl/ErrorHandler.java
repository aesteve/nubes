package io.vertx.mvc.handlers.impl;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.impl.Utils;
import io.vertx.mvc.Config;
import io.vertx.mvc.exceptions.HttpException;
import io.vertx.mvc.views.TemplateEngineManager;

public class ErrorHandler implements Handler<RoutingContext> {

	private final static Logger log = LoggerFactory.getLogger(ErrorHandler.class);
	private final static String DEFAULT_ERROR_MSG = "Internal server error";
	
	private Config config;
	private Map<Integer, String> errorTemplates;
	private TemplateEngineManager templManager;
	
	public ErrorHandler(Config config, TemplateEngineManager templManager) {
		this.config = config;
		this.templManager = templManager;
		errorTemplates = new HashMap<Integer, String>();
		addDefaultErrorPages();
	}
	
	@Override
	public void handle(RoutingContext context) {
		Vertx vertx = context.vertx();
		Throwable cause = context.failure();
		HttpServerResponse response = context.response();
		log.error("Error caught in routing context", cause);
		if (cause instanceof HttpException) {
			HttpException he = (HttpException)cause;
			response.setStatusCode(he.getStatusCode());
			response.setStatusMessage(he.getMessage());
			if (isView(context)) {
				String tpl = errorTemplates.get(he.getStatusCode());
				if (tpl != null) {
					try {
						response.end(Utils.readFileToString(vertx, tpl));
					} catch(VertxException ve) {
						log.error("Could not read error template : "+tpl, ve);
						response.end(DEFAULT_ERROR_MSG);
					}
				} else {
					response.end(he.getMessage());
				}
			}
			else {
				// find the best marshaller
				// marshall error
			}
		} else if (cause != null) {
			response.setStatusCode(500);
			if (isView(context)) {
				String tplFile = errorTemplates.get(500);
				try {
					String tpl = Utils.readFileToString(vertx, tplFile);
					if (config.displayErrors) {
						tplFile.replace("{errorMsg}", cause.getMessage());
						StringBuilder sb = new StringBuilder();
						tplFile.replace("{stack}", createStackTrace(sb, cause));
					} else {
						tplFile.replace("{errorMsg}", DEFAULT_ERROR_MSG);
						tplFile.replace("{stack}", "");
					}
					
				} catch(VertxException ve) {
					log.error("Could not read error template : "+tplFile, ve);
					response.end(DEFAULT_ERROR_MSG);
				}
			}
		} else {
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
	
	private StringBuilder createStackTrace(StringBuilder sb, Throwable t) {
		sb.append("<div class=\"exception\">");
		sb.append("<div class=\"exception-msg\">" + t.getMessage() + "</div>");
		sb.append("<ul class=\"stacktrace\">");
		for (StackTraceElement ste : t.getStackTrace()) {
			sb.append("<li>" + ste.toString() + "</li>");
		}
		sb.append("</ul>");
		sb.append("</div>");
		if (t.getCause() != null) {
			createStackTrace(sb, t.getCause());
		}
		return sb;
	}
	
}
