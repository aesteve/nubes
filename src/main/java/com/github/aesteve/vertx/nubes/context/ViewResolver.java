package com.github.aesteve.vertx.nubes.context;

import io.vertx.ext.web.RoutingContext;

public class ViewResolver {

	private static final String CONTEXT_TPL_NAME = "tpl-name";

	public static void resolve(RoutingContext context, String viewName) {
		context.put(CONTEXT_TPL_NAME, viewName);
	}

	public static String getViewName(RoutingContext context) {
		return context.get(CONTEXT_TPL_NAME);
	}
}
