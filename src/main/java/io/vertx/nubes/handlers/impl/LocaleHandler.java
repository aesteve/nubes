package io.vertx.nubes.handlers.impl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.i18n.LocaleResolverRegistry;
import io.vertx.nubes.reflections.injectors.typed.impl.LocaleParamInjector;

import java.util.Locale;

public class LocaleHandler implements Handler<RoutingContext> {

    private final LocaleResolverRegistry localeResolverRegistry;

    public LocaleHandler(LocaleResolverRegistry localeResolverRegistry) {
        this.localeResolverRegistry = localeResolverRegistry;
    }

    @Override
    public void handle(RoutingContext context) {
        Locale loc = localeResolverRegistry.resolve(context);
        if (loc != null) {
            context.put(LocaleParamInjector.LOCALE_ATTR, loc.toLanguageTag());
            context.response().headers().add("Content-Language", loc.toLanguageTag());
        }
        context.next();
    }

}
