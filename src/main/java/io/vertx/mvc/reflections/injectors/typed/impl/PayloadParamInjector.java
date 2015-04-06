package io.vertx.mvc.reflections.injectors.typed.impl;

import io.vertx.mvc.marshallers.Payload;
import io.vertx.mvc.reflections.injectors.typed.ContextDataParamInjector;

public class PayloadParamInjector<T> extends ContextDataParamInjector<Payload<T>> {

	@Override
	protected String dataAttr() {
		return Payload.DATA_ATTR;
	}

}
