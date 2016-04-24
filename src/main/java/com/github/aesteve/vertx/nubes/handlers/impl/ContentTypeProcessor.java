package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class ContentTypeProcessor extends NoopAfterAllProcessor implements AnnotationProcessor<ContentType> {

  public static final String BEST_CONTENT_TYPE = "nubes-best-content-type";

  private final ContentType annotation;

  public ContentTypeProcessor(ContentType annotation) {
    this.annotation = annotation;
  }

  @Override
  public void preHandle(RoutingContext context) {
    List<String> contentTypes = Arrays.asList(annotation.value());
    String accept = context.request().getHeader(ACCEPT.toString());
    if (accept == null) {
      context.fail(406);
      return;
    }
    List<String> acceptableTypes = Utils.getSortedAcceptableMimeTypes(accept);
    Optional<String> bestType = acceptableTypes.stream().filter(contentTypes::contains).findFirst();
    if (bestType.isPresent()) {
      ContentTypeProcessor.setContentType(context, bestType.get());
      context.next();
    } else {
      context.fail(406);
    }
  }

  @Override
  public void postHandle(RoutingContext context) {
    context.response().putHeader(CONTENT_TYPE, ContentTypeProcessor.getContentType(context));
    context.next();
  }

  public static String getContentType(RoutingContext context) {
    return context.get(BEST_CONTENT_TYPE);
  }

  private static void setContentType(RoutingContext context, String contentType) {
    context.put(BEST_CONTENT_TYPE, contentType);
  }

}
