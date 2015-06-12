package com.github.aesteve.vertx.nubes.reflections.injectors.typed;

import io.vertx.ext.web.RoutingContext;

public abstract class ContextDataParamInjector<T> implements ParamInjector<T> {

    protected abstract String dataAttr();

    @Override
    public T resolve(RoutingContext context) {
        return context.get(dataAttr());
    }

}
