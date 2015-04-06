package io.vertx.mvc.reflections.injectors.typed.impl;

import io.vertx.mvc.context.PaginationContext;
import io.vertx.mvc.reflections.injectors.typed.ContextDataParamInjector;

public class PaginationContextParamInjector extends ContextDataParamInjector<PaginationContext> {
	
	protected String dataAttr() {
		return PaginationContext.DATA_ATTR;
	}
	
}
