package com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl;

import com.github.aesteve.vertx.nubes.context.PaginationContext;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ContextBasedParamInjector;

public class PaginationContextParamInjector extends ContextBasedParamInjector<PaginationContext> {

	protected String dataAttr() {
		return PaginationContext.DATA_ATTR;
	}

}
