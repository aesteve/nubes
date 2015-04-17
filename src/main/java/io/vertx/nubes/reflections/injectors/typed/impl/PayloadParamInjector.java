package io.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.nubes.marshallers.Payload;
import io.vertx.nubes.reflections.injectors.typed.ContextDataParamInjector;

public class PayloadParamInjector<T> extends ContextDataParamInjector<Payload<T>> {

	@Override
	protected String dataAttr() {
		return Payload.DATA_ATTR;
	}

}
