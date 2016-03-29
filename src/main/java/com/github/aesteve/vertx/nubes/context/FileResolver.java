package com.github.aesteve.vertx.nubes.context;

import io.vertx.ext.web.RoutingContext;

public class FileResolver {

	private static final String CONTEXT_FILE_NAME = "file-name";

	public static void resolve(RoutingContext context, String fileName) {
		context.put(CONTEXT_FILE_NAME, fileName);
	}

	public static String getFileName(RoutingContext context) {
		return context.get(CONTEXT_FILE_NAME);
	}
}
