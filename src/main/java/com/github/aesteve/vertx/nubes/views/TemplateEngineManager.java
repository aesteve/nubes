package com.github.aesteve.vertx.nubes.views;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.context.ViewResolver;
import com.github.aesteve.vertx.nubes.utils.Utils;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.TemplateEngine;
import org.apache.commons.io.FilenameUtils;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class TemplateEngineManager implements TemplateHandler {

  private static final Logger LOG = LoggerFactory.getLogger(TemplateEngineManager.class);

  private final Config config;

  public TemplateEngineManager(Config config) {
    this.config = config;
  }

  public TemplateEngine fromViewName(String tplName) {
    String extension = FilenameUtils.getExtension(tplName);
    return config.getTemplateEngines().get(extension);
  }

  @Override
  public void handle(RoutingContext context) {

    String tplDir = Utils.normalizePath(config.getTplDir());
    String tplFile = ViewResolver.getViewName(context);

    TemplateEngine engine = fromViewName(tplDir + tplFile);
    if (engine == null) {
      LOG.error("No template handler found for " + tplDir + tplFile);
      context.fail(500);
      return;
    }
    engine.render(context, tplDir, tplFile, res -> {
      if (res.succeeded()) {
        context.response().putHeader(CONTENT_TYPE, "text/html").end(res.result());
      } else {
        context.fail(res.cause());
      }
    });
  }

  @Override
  public TemplateHandler setIndexTemplate(String indexTemplate) {
    return this;
  }
}
