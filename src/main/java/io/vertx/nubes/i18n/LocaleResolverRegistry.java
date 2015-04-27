package io.vertx.nubes.i18n;

import io.vertx.ext.apex.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocaleResolverRegistry {
    private List<LocaleResolver> resolvers;
    private final List<Locale> availableLocales;
    private Locale defaultLocale;

    public LocaleResolverRegistry(Locale loc) {
        resolvers = new ArrayList<LocaleResolver>();
        availableLocales = new ArrayList<Locale>(1);
        availableLocales.add(loc);
    }

    public LocaleResolverRegistry(List<Locale> availableLocales) {
        resolvers = new ArrayList<LocaleResolver>();
        this.availableLocales = availableLocales;
    }

    public void addLocales(List<Locale> locales) {
        availableLocales.addAll(locales);
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
