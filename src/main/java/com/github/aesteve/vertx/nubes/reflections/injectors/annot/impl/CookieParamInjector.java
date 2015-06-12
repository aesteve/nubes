package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

public class CookieParamInjector implements AnnotatedParamInjector<CookieValue> {

    @Override
    public Object resolve(RoutingContext context, CookieValue annotation, Class<?> resultClass) throws BadRequestException {
        Cookie cookie = context.getCookie(annotation.value());
        if (resultClass.equals(Cookie.class) && cookie != null) {
            return cookie;
        } else if (resultClass.equals(String.class) && cookie != null) {
            return cookie.getValue();
        } else if (cookie == null) {
            throw new BadRequestException("Cookie : " + annotation.value() + " must be set");
        }
        return null;
    }

}
