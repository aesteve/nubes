package com.github.aesteve.vertx.nubes.reflections.adapters.impl;

import io.vertx.core.MultiMap;

import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapter;
import com.github.aesteve.vertx.nubes.utils.DateUtils;

public class DefaultParameterAdapter implements ParameterAdapter<Object> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object adaptParam(String value, Class<?> parameterClass) throws Exception {
		if (value == null) {
			return null;
		}
		if (parameterClass.equals(String.class)) {
			return value;
		} else if (parameterClass.equals(Long.class)) {
			return Long.valueOf(value);
		} else if (parameterClass.equals(Integer.class)) {
			return Integer.valueOf(value);
		} else if (parameterClass.equals(Float.class)) {
			return Float.valueOf(value);
		} else if (parameterClass.equals(Date.class)) {
			return DateUtils.INSTANCE.parseIso8601(value);
		} else if (parameterClass.isEnum()) {
			return Enum.valueOf((Class<Enum>) parameterClass, value);
		}
		return null;
	}

	public Object adaptParams(MultiMap params, Class<?> parameterClass) throws Exception {
		Object instance = parameterClass.newInstance();
		Field[] fields = parameterClass.getDeclaredFields();
		for (Field field : fields) {
			String requestValue = params.get(field.getName());
			if (requestValue != null) {
				Object value = adaptParam(requestValue, field.getType());
				PropertyUtils.setProperty(instance, field.getName(), value);
			}
		}
		return instance;
	}
}
