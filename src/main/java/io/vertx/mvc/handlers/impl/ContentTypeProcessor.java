package io.vertx.mvc.handlers.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.impl.Utils;
import io.vertx.mvc.annotations.mixins.ContentType;
import io.vertx.mvc.handlers.AnnotationProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ContentTypeProcessor implements AnnotationProcessor<ContentType> {

	private List<String> contentTypes;

	public void init(ContentType contentType) {
		this.contentTypes = Arrays.asList(contentType.value());
	}

	@Override
	public void preHandle(RoutingContext context) {
		String accept = context.request().getHeader("accept");
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
