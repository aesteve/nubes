package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.params.LocalMapValue;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

public class LocalMapValueParamInjector implements AnnotatedParamInjector<LocalMapValue> {

	@Override
	public Object resolve(RoutingContext context, LocalMapValue annotation, Class<?> resultClass) {
		SharedData sd = context.vertx().sharedData();
		io.vertx.core.shareddata.LocalMap<Object, Object> map = sd.getLocalMap(annotation.mapName());
		return map.get(annotation.key());
	}

}
