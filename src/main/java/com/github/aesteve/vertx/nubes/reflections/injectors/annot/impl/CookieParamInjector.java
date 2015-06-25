package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class CookieParamInjector implements AnnotatedParamInjector<CookieValue> {

	@Override
	public Object resolve(RoutingContext context, CookieValue annotation, Class<?> resultClass) {
		Cookie cookie = context.getCookie(annotation.value());
		if (resultClass.equals(Cookie.class) && cookie != null) {
			return cookie;
		} else if (resultClass.equals(String.class) && cookie != null) {
			return cookie.getValue();
		} else if (cookie == null) {
			DefaultErrorHandler.badRequest(context, "Cookie " + annotation.value() + " must be set");
		}
		return null;
	}

}
