package com.github.aesteve.vertx.nubes.reflections.adapters;

import io.vertx.core.MultiMap;

public interface ParameterAdapter<T> {

  T adaptParam(String value, Class<? extends T> parameterClass);

  T adaptParams(MultiMap map, Class<? extends T> parameterClass);

}
