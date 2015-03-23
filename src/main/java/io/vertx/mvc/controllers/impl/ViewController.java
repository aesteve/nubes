package io.vertx.mvc.controllers.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.templ.TemplateEngine;
import io.vertx.mvc.controllers.AbstractController;
import io.vertx.mvc.views.TemplateUtils;

public class ViewController extends AbstractController {
	
	protected void renderView(RoutingContext context, String view) {
		TemplateEngine engine = TemplateUtils.fromViewName(view);
		engine.render(context, view, null);
	}
}
