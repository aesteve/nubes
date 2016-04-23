package com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl;

import com.github.aesteve.vertx.nubes.annotations.params.Header;
import com.github.aesteve.vertx.nubes.exceptions.params.InvalidParamValueException;
import com.github.aesteve.vertx.nubes.exceptions.params.MandatoryParamException;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException.ParamType;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import io.vertx.ext.web.RoutingContext;

public class HeaderParamInjector implements AnnotatedParamInjector<Header> {

  private final ParameterAdapterRegistry registry;

  public HeaderParamInjector(ParameterAdapterRegistry registry) {
    this.registry = registry;
  }

  @Override
  public Object resolve(RoutingContext context, Header annotation, String paramName, Class<?> resultClass) throws WrongParameterException {
    String headerName = annotation.value();
    if ("".equals(headerName)) {
      headerName = paramName;
    }
    String headerValue = context.request().getHeader(headerName);
    if (headerValue == null) {
      if (annotation.mandatory()) {
        throw new MandatoryParamException(ParamType.HEADER, headerName);
      }
    }
    try {
      return registry.adaptParam(headerValue, resultClass);
    } catch (IllegalArgumentException iae) {
      throw new InvalidParamValueException(ParamType.HEADER, headerName, headerValue, iae);
    }
  }

}
