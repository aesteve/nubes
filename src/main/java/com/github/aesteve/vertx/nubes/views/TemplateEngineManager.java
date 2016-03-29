package com.github.aesteve.vertx.nubes.views;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.TemplateEngine;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.context.ViewResolver;

public class TemplateEngineManager implements TemplateHandler {

	private final static Logger log = LoggerFactory.getLogger(TemplateEngineManager.class);

	private final Config config;

	public TemplateEngineManager(Config config) {
		this.config = config;
	}

	public TemplateEngine fromViewName(String tplName) {
		int pointIdx = tplName.lastIndexOf(".");
		String extension = tplName.substring(pointIdx + 1, tplName.length());
		return config.templateEngines.get(extension);
	}

	@Override
	public void handle(RoutingContext context) {
		String tplName = normalize(config.tplDir) + ViewResolver.getViewName(context);
		TemplateEngine engine = fromViewName(tplName);
		if (engine == null) {
			log.error("No template handler found for " + tplName);
			context.fail(500);
			return;
		}
		engine.render(context, tplName, res -> {
			if (res.succeeded()) {
				context.response().putHeader(CONTENT_TYPE, "text/html").end(res.result());
			} else {
				context.fail(res.cause());
			}
		});
	}

	private static String normalize(String dir) {
		if (!dir.endsWith("/")) {
			dir += "/";
		}
		return dir;
	}
}
