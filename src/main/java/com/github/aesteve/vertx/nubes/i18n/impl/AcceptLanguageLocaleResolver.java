package com.github.aesteve.vertx.nubes.i18n.impl;

import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import com.github.aesteve.vertx.nubes.i18n.LocaleResolver;

import io.vertx.ext.web.RoutingContext;
import static io.vertx.core.http.HttpHeaders.*;

public class AcceptLanguageLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolve(RoutingContext context, List<Locale> availableLocales) {
        String accept = context.request().getHeader(ACCEPT_LANGUAGE.toString());
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
