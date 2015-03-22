package io.vertx.mvc.controllers.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.templ.TemplateEngine;
import io.vertx.mvc.views.TemplateUtils;

public class ViewController {
	
	protected void renderView(RoutingContext context, String view) {
		TemplateEngine engine = TemplateUtils.fromName(view);
		engine.render(context, view, null);
	}
}
