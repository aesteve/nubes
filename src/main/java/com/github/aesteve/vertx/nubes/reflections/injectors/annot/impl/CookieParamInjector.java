package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class CookieParamInjector implements AnnotatedParamInjector<CookieValue> {

	@Override
	public Object resolve(RoutingContext context, CookieValue annotation, String paramName, Class<?> resultClass) {
		String cookieName = annotation.value();
		if ("".equals(cookieName)) {
			cookieName = paramName;
		}
		Cookie cookie = context.getCookie(cookieName);
		if (resultClass.equals(Cookie.class) && cookie != null) {
			return cookie;
		} else if (resultClass.equals(String.class) && cookie != null) {
			return cookie.getValue();
		} else if (cookie == null) {
			DefaultErrorHandler.badRequest(context, "Cookie " + cookieName + " must be set");
		}
		return null;
	}

}
