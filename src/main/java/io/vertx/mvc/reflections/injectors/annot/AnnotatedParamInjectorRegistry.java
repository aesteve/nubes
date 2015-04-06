package io.vertx.mvc.reflections.injectors.annot;

import io.vertx.mvc.annotations.cookies.CookieValue;
import io.vertx.mvc.annotations.params.Header;
import io.vertx.mvc.annotations.params.Param;
import io.vertx.mvc.annotations.params.Params;
import io.vertx.mvc.annotations.params.PathParam;
import io.vertx.mvc.annotations.params.RequestBody;
import io.vertx.mvc.marshallers.PayloadMarshaller;
import io.vertx.mvc.reflections.adapters.ParameterAdapterRegistry;
import io.vertx.mvc.reflections.injectors.annot.impl.CookieParamInjector;
import io.vertx.mvc.reflections.injectors.annot.impl.HeaderParamInjector;
import io.vertx.mvc.reflections.injectors.annot.impl.ParamsInjector;
import io.vertx.mvc.reflections.injectors.annot.impl.PathParamInjector;
import io.vertx.mvc.reflections.injectors.annot.impl.RequestBodyParamInjector;
import io.vertx.mvc.reflections.injectors.annot.impl.ParamInjector;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class AnnotatedParamInjectorRegistry {
	
	private Map<Class<?>, AnnotatedParamInjector<?>> map;
	
	public AnnotatedParamInjectorRegistry(Map<String, PayloadMarshaller> marshallers, ParameterAdapterRegistry adapters) {
		map = new HashMap<Class<?>, AnnotatedParamInjector<?>>();
		registerInjector(RequestBody.class, new RequestBodyParamInjector(marshallers));
		registerInjector(CookieValue.class, new CookieParamInjector());
		registerInjector(Header.class, new HeaderParamInjector(adapters));
		registerInjector(Param.class, new ParamInjector(adapters));
		registerInjector(Params.class, new ParamsInjector(adapters));
		registerInjector(PathParam.class, new PathParamInjector(adapters));
	}
	
	public<T extends Annotation> void registerInjector(Class<? extends T> clazz, AnnotatedParamInjector<T> injector) {
		map.put(clazz, injector);
	}
	
	@SuppressWarnings("unchecked")
	public<T extends Annotation> AnnotatedParamInjector<T> getInjector(Class<? extends T> clazz) {
		return (AnnotatedParamInjector<T>)map.get(clazz);
	}

}
