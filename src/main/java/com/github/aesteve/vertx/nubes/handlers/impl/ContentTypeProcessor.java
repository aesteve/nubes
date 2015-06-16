package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.Utils;
import static io.vertx.core.http.HttpHeaders.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

public class ContentTypeProcessor implements AnnotationProcessor<ContentType> {

    public final static String BEST_CONTENT_TYPE = "nubes-best-content-type";

    private ContentType annotation;

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
        Optional<String> bestType = acceptableTypes.stream().filter(type -> {
            return contentTypes.contains(type);
        }).findFirst();
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

    @Override
    public Class<? extends ContentType> getAnnotationType() {
        return ContentType.class;
    }

    public static String getContentType(RoutingContext context) {
        return context.get(BEST_CONTENT_TYPE);
    }

    public static void setContentType(RoutingContext context, String contentType) {
        context.put(BEST_CONTENT_TYPE, contentType);
    }

}
