package io.vertx.nubes.reflections.adapters;

import io.vertx.core.MultiMap;

public interface ParameterAdapter<T> {
	public T adaptParam(String value, Class<? extends T> parameterClass) throws Exception;
	public T adaptParams(MultiMap map, Class<? extends T> parameterClass) throws Exception;
}
