package com.github.aesteve.vertx.nubes.views;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.ext.web.templ.JadeTemplateEngine;
import io.vertx.ext.web.templ.MVELTemplateEngine;
import io.vertx.ext.web.templ.TemplateEngine;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.context.ViewResolver;
import com.github.aesteve.vertx.nubes.views.impl.PrefixedHandlebarsTemplateEngineImpl;

public class TemplateEngineManager implements TemplateHandler {

	private TemplateEngine defaultEngine;

	private HandlebarsTemplateEngine hbsEngine;
	private MVELTemplateEngine mvelEngine;
	private JadeTemplateEngine jadeEngine;
	private ThymeleafTemplateEngine thymeleafEngine;
	private String tplDir;

	public TemplateEngineManager(Config config) {
		this.tplDir = config.tplDir;
		if (!this.tplDir.endsWith("/")) {
			this.tplDir += "/";
		}
		hbsEngine = new PrefixedHandlebarsTemplateEngineImpl(tplDir);
		hbsEngine.setMaxCacheSize(0);
		mvelEngine = MVELTemplateEngine.create();
		mvelEngine.setMaxCacheSize(0);
		jadeEngine = JadeTemplateEngine.create();
		jadeEngine.setMaxCacheSize(0);
		thymeleafEngine = ThymeleafTemplateEngine.create();
		defaultEngine = thymeleafEngine;
	}

	public TemplateEngine fromViewName(String tplName) {
		int pointIdx = tplName.lastIndexOf(".");
		if (pointIdx == -1) {
			return defaultEngine;
		}
		String extension = tplName.substring(pointIdx + 1, tplName.length());
		switch (extension) {
		case "hbs":
			return hbsEngine;
		case "templ":
			return mvelEngine;
		case "jade":
			return jadeEngine;
		case "html":
			return thymeleafEngine;
		}
		return defaultEngine;
	}

	@Override
	public void handle(RoutingContext context) {
		String tplName = ViewResolver.getViewName(context);
		TemplateEngine engine = fromViewName(tplName);
		engine.render(context, tplName, res -> {
			if (res.succeeded()) {
				context.response().putHeader(CONTENT_TYPE, "text/html").end(res.result());
			} else {
				context.fail(res.cause());
			}
		});
	}
}
