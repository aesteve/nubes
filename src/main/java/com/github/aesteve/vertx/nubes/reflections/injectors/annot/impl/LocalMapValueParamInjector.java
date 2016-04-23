package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.LocalMapValue;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;

public class LocalMapValueParamInjector implements AnnotatedParamInjector<LocalMapValue> {

  @Override
  public Object resolve(RoutingContext context, LocalMapValue annotation, String paramName, Class<?> resultClass) {
    SharedData sd = context.vertx().sharedData();
    String mapName = annotation.mapName();
    String key = annotation.key();
    if ("".equals(key)) {
      key = paramName;
    }
    io.vertx.core.shareddata.LocalMap<Object, Object> map = sd.getLocalMap(mapName);
    return map.get(key);
  }

}
