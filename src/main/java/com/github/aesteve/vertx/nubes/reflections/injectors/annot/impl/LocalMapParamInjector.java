package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.VertxLocalMap;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;

public class LocalMapParamInjector implements AnnotatedParamInjector<VertxLocalMap> {

  @Override
  public Object resolve(RoutingContext context, VertxLocalMap annotation, String paramName, Class<?> resultClass) {
    SharedData sd = context.vertx().sharedData();
    String mapName = annotation.value();
    if ("".equals(mapName)) {
      mapName = paramName;
    }
    return sd.getLocalMap(mapName);
  }

}
