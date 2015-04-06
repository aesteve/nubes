package io.vertx.mvc.reflections.injectors.typed;

import io.vertx.core.Vertx;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.context.PaginationContext;
import io.vertx.mvc.marshallers.Payload;
import io.vertx.mvc.reflections.injectors.typed.impl.PaginationContextParamInjector;
import io.vertx.mvc.reflections.injectors.typed.impl.PayloadParamInjector;
import io.vertx.mvc.reflections.injectors.typed.impl.RoutingContextParamInjector;
import io.vertx.mvc.reflections.injectors.typed.impl.VertxParamInjector;

import java.util.HashMap;
import java.util.Map;

public class TypedParamInjectorRegistry {
	
	private Map<Class<?>, ParamInjector<?>> map;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TypedParamInjectorRegistry() {
		map = new HashMap<Class<?>, ParamInjector<?>>();
		registerInjector(Vertx.class, new VertxParamInjector());
		registerInjector(RoutingContext.class, new RoutingContextParamInjector());
		registerInjector(Payload.class, new PayloadParamInjector());
		registerInjector(PaginationContext.class, new PaginationContextParamInjector());
	}
	
	public<T> void registerInjector(Class<? extends T> clazz, ParamInjector<T> injector) {
		map.put(clazz, injector);
	}
	
	@SuppressWarnings("unchecked")
	public<T> ParamInjector<T> getInjector(Class<? extends T> clazz) {
		return (ParamInjector<T>)map.get(clazz);
	}
}
