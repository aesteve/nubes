package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.marshallers.Payload;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ContextBasedParamInjector;

public class PayloadParamInjector<T> extends ContextBasedParamInjector<Payload<T>> {

  @Override
  protected String dataAttr() {
    return Payload.DATA_ATTR;
  }

}
