package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import io.vertx.ext.web.RoutingContext;

import java.util.Locale;

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
