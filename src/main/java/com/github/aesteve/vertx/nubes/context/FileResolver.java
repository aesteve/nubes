package com.github.aesteve.vertx.nubes.context;

import io.vertx.ext.web.RoutingContext;

import static com.github.aesteve.vertx.nubes.context.FileResolver.Constants.CONTEXT_FILE_NAME;

public interface FileResolver {

  final class Constants {
    final static String CONTEXT_FILE_NAME = "file-name";
  }

  static void resolve(RoutingContext context, String fileName) {
    context.put(CONTEXT_FILE_NAME, fileName);
  }

  static String getFileName(RoutingContext context) {
    return context.get(CONTEXT_FILE_NAME);
  }

}
