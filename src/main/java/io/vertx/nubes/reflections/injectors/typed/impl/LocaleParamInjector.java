package io.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.nubes.reflections.injectors.typed.ParamInjector;

import java.util.Locale;

public class LocaleParamInjector implements ParamInjector<Locale> {

    public final static String LOCALE_ATTR = "user-locale";

    @Override
    public Locale resolve(RoutingContext context) {
        String tag = context.get(LOCALE_ATTR);
        if (tag == null) {
            return null;
        }
        return Locale.forLanguageTag(tag);
    }

}
