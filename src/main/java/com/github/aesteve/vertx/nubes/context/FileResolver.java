package com.github.aesteve.vertx.nubes.context;

import io.vertx.ext.web.RoutingContext;

public interface FileResolver {

  String CONTEXT_FILE_NAME = "file-name";

  static void resolve(RoutingContext context, String fileName) {
    context.put(CONTEXT_FILE_NAME, fileName);
  }

  static String getFileName(RoutingContext context) {
    return context.get(CONTEXT_FILE_NAME);
  }

}
