package com.github.aesteve.vertx.nubes.i18n;

import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Locale;

public interface LocaleResolver {
	public Locale resolve(RoutingContext context, List<Locale> availableLocales);
}
