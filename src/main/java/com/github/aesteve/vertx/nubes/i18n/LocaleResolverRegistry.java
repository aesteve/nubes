package com.github.aesteve.vertx.nubes.i18n;

import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocaleResolverRegistry {
	private List<LocaleResolver> resolvers;
	private final List<Locale> availableLocales;
	private Locale defaultLocale;

	public LocaleResolverRegistry(Locale loc) {
		resolvers = new ArrayList<>();
		availableLocales = new ArrayList<>();
		availableLocales.add(loc);
	}

	public LocaleResolverRegistry(List<Locale> availableLocales) {
		resolvers = new ArrayList<>();
		this.availableLocales = availableLocales;
	}

	public void addLocales(List<Locale> locales) {
		availableLocales.addAll(locales);
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public void addResolver(LocaleResolver resolver) {
		this.resolvers.add(resolver);
	}

	public void removeResolver(LocaleResolver resolver) {
		this.resolvers.remove(resolver);
	}

	public List<Locale> getAvailableLocales() {
		return availableLocales;
	}

	public Locale resolve(RoutingContext context) {
		for (LocaleResolver resolver : resolvers) {
			Locale loc = resolver.resolve(context, availableLocales);
			if (loc != null) {
				return loc;
			}
		}
		return defaultLocale;
	}
}
