package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.exceptions.params.InvalidParamValueException;
import com.github.aesteve.vertx.nubes.exceptions.params.MandatoryParamException;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException.ParamType;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

public class ParamInjector implements AnnotatedParamInjector<Param> {

  private final ParameterAdapterRegistry adapters;

  public ParamInjector(ParameterAdapterRegistry adapters) {
    this.adapters = adapters;
  }

  @Override
  public Object resolve(RoutingContext context, Param annotation, String paramName, Class<?> resultClass) throws WrongParameterException {
    final String requestParamName = StringUtils.isEmpty(annotation.value()) ? paramName : annotation.value();
    final String paramValue = context.request().getParam(requestParamName);
    if (paramValue == null && annotation.mandatory()) {
      throw new MandatoryParamException(ParamType.REQUEST_PARAM, requestParamName);
    }
    try {
      return adapters.adaptParam(paramValue, resultClass);
    } catch (IllegalArgumentException iae) {
      throw new InvalidParamValueException(ParamType.REQUEST_PARAM, requestParamName, paramValue, iae);
    }
  }

}
