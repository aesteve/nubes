package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.ext.web.RoutingContext;

import java.util.Locale;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

public class LocaleParamInjector implements ParamInjector<Locale> {

	public final static String LOCALE_ATTR = "nubes-user-locale";

	@Override
	public Locale resolve(RoutingContext context) {
		String tag = context.get(LOCALE_ATTR);
		if (tag == null) {
			return null;
		}
		return Locale.forLanguageTag(tag);
	}

}
