package io.vertx.mvc.handlers.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.impl.Utils;
import io.vertx.mvc.annotations.mixins.ContentType;
import io.vertx.mvc.handlers.AnnotationProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ContentTypeProcessor implements AnnotationProcessor<ContentType> {

	public void init(RoutingContext context, ContentType contentType) {
		context.put("content-types", Arrays.asList(contentType.value()));
	}

	@Override
	public void preHandle(RoutingContext context) {
		String accept = context.request().getHeader("accept");
		List<String> contentTypes = context.get("content-types");
		if (accept == null) {
			context.fail(406);
			return;
		}
		List<String> acceptableTypes = Utils.getSortedAcceptableMimeTypes(accept);
		Optional<String> bestType = acceptableTypes.stream().filter(type -> {
			return contentTypes.contains(type);
		}).findFirst();
		if (bestType.isPresent()) {
			context.put("best-content-type", bestType.get());
			context.next();
		} else {
			context.fail(406);
		}
	}

	@Override
	public void postHandle(RoutingContext context) {
		context.response().putHeader("Content-Type", (String)context.get("best-content-type"));
		context.next();
	}

	@Override
	public Class<? extends ContentType> getAnnotationType() {
		return ContentType.class;
	}

}
