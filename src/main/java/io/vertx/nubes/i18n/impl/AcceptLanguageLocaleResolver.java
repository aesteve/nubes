package io.vertx.nubes.i18n.impl;

import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.nubes.i18n.LocaleResolver;

public class AcceptLanguageLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolve(RoutingContext context, List<Locale> availableLocales) {
        String accept = context.request().getHeader("Accept-Language");
        if (accept == null) {
            return null;
        }
        List<LanguageRange> ranges = LanguageRange.parse(accept);
        if (ranges.isEmpty()) {
            return null;
        }
        return Locale.lookup(ranges, availableLocales);
    }

}
