package com.github.aesteve.vertx.nubes.reflections.injectors.annot;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.annotations.auth.User;
import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.annotations.params.ContextData;
import com.github.aesteve.vertx.nubes.annotations.params.Header;
import com.github.aesteve.vertx.nubes.annotations.params.Headers;
import com.github.aesteve.vertx.nubes.annotations.params.LocalMapValue;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.annotations.params.VertxLocalMap;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.ContextDataParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.CookieParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.HeaderParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.HeadersParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.LocalMapParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.LocalMapValueParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.ParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.ParamsInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.RequestBodyParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.impl.UserParamInjector;

public class AnnotatedParamInjectorRegistry {

	private final Map<Class<?>, AnnotatedParamInjector<?>> map;

	public AnnotatedParamInjectorRegistry(Map<String, PayloadMarshaller> marshallers, ParameterAdapterRegistry adapters) {
		map = new HashMap<>();
		registerInjector(RequestBody.class, new RequestBodyParamInjector(marshallers));
		registerInjector(CookieValue.class, new CookieParamInjector());
		registerInjector(Header.class, new HeaderParamInjector(adapters));
		registerInjector(Param.class, new ParamInjector(adapters));
		registerInjector(Params.class, new ParamsInjector(adapters));
		registerInjector(User.class, new UserParamInjector());
		registerInjector(LocalMapValue.class, new LocalMapValueParamInjector());
		registerInjector(VertxLocalMap.class, new LocalMapParamInjector());
		registerInjector(ContextData.class, new ContextDataParamInjector());
		registerInjector(Headers.class, new HeadersParamInjector());
	}

	public <T extends Annotation> void registerInjector(Class<? extends T> clazz, AnnotatedParamInjector<T> injector) {
		map.put(clazz, injector);
	}

	@SuppressWarnings("unchecked")
	public <T extends Annotation> AnnotatedParamInjector<T> getInjector(Class<? extends T> clazz) {
		return (AnnotatedParamInjector<T>) map.get(clazz);
	}

}
