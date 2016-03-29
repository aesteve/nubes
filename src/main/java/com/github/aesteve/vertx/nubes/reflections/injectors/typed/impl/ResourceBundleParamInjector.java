package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.ext.web.RoutingContext;

import java.util.Locale;
import java.util.ResourceBundle;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

public class ResourceBundleParamInjector implements ParamInjector<ResourceBundle> {

	private final Config config;

	public ResourceBundleParamInjector(Config config) {
		this.config = config;
	}

	@Override
	public ResourceBundle resolve(RoutingContext context) {
		String tag = context.get(LocaleParamInjector.LOCALE_ATTR);
		if (tag == null) {
			return null;
		}
		return config.getResourceBundle(Locale.forLanguageTag(tag));
	}

}
