package com.github.aesteve.vertx.nubes.reflections.adapters.impl;

import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapter;
import com.github.aesteve.vertx.nubes.utils.DateUtils;
import io.vertx.core.MultiMap;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultParameterAdapter implements ParameterAdapter<Object> {

  private final static Map<Class<?>, Function<String, Object>> adapters = new HashMap<>();

  static {
    adapters.put(String.class, String::valueOf);
    adapters.put(Long.class, Long::valueOf);
    adapters.put(Integer.class, Integer::valueOf);
    adapters.put(Float.class, Float::valueOf);
    adapters.put(Date.class, DateUtils.INSTANCE::parseIso8601);
  }

  @SuppressWarnings("unchecked")
  public Object adaptParam(String value, Class<?> parameterClass) {
    if (value == null) {
      return null;
    }
    Function<String, Object> adapter = adapters.get(parameterClass);
    if (adapter != null) {
      return adapter.apply(value);
    } else if (parameterClass.isEnum()) {
      return Enum.valueOf((Class<Enum>) parameterClass, value);
    }
    return null;
  }

  public Object adaptParams(MultiMap params, Class<?> parameterClass) {
    Object instance = null;
    try {
      instance = parameterClass.newInstance();
      Field[] fields = parameterClass.getDeclaredFields();
      for (Field field : fields) {
        String requestValue = params.get(field.getName());
        if (requestValue != null) {
          Object value = adaptParam(requestValue, field.getType());
          PropertyUtils.setProperty(instance, field.getName(), value);
        }
      }
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalArgumentException(e);
    }
    return instance;
  }
}
