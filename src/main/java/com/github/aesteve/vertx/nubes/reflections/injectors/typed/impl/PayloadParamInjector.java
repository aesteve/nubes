package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.marshallers.Payload;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ContextDataParamInjector;

public class PayloadParamInjector<T> extends ContextDataParamInjector<Payload<T>> {

	@Override
	protected String dataAttr() {
		return Payload.DATA_ATTR;
	}

}
