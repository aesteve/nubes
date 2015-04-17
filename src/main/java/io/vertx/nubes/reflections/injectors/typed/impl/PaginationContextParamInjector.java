package io.vertx.nubes.reflections.injectors.typed.impl;

import io.vertx.nubes.context.PaginationContext;
import io.vertx.nubes.reflections.injectors.typed.ContextDataParamInjector;

public class PaginationContextParamInjector extends ContextDataParamInjector<PaginationContext> {
	
	protected String dataAttr() {
		return PaginationContext.DATA_ATTR;
	}
	
}
