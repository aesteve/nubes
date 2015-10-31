package com.github.aesteve.vertx.nubes.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractMethodInvocationHandler<T> implements Handler<RoutingContext> {

	protected Method method;
	protected Object instance;
	protected Config config;
	protected Parameter[] parameters;
	protected boolean usesRoutingContext;
	protected boolean hasNext;
	protected BiConsumer<RoutingContext, T> returnHandler;
	protected boolean returnsSomething;

	public AbstractMethodInvocationHandler(Object instance, Method method, Config config, boolean hasNext, BiConsumer<RoutingContext, T> returnHandler) {
		this.method = method;
		returnsSomething = !method.getReturnType().equals(Void.TYPE);
		this.hasNext = hasNext;
		parameters = method.getParameters();
		for (Parameter param : parameters) {
			if (param.getType().equals(RoutingContext.class)) {
				usesRoutingContext = true;
				break;
			}
		}
		this.config = config;
		this.instance = instance;
		this.returnHandler = returnHandler;
	}

	@Override
	abstract public void handle(RoutingContext routingContext);

	protected Object[] getParameters(RoutingContext routingContext) throws WrongParameterException {
		List<Object> params = new ArrayList<>();
		for (Parameter param : parameters) {
			Object paramInstance = getParameterInstance(routingContext, param.getAnnotations(), param.getType(), param.getName());
			params.add(paramInstance);
		}
		return params.toArray();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass, String paramName) throws WrongParameterException {
		if (annotations.length == 0) { // rely on type
			ParamInjector<?> injector = config.typeInjectors.getInjector(parameterClass);
			if (injector == null) {
				return null;
			}
			return injector.resolve(context);
		}
		if (annotations.length > 1) {
			throw new IllegalArgumentException("Every parameter should only have ONE annotation");
		}
		Annotation annotation = annotations[0]; // rely on annotation
		AnnotatedParamInjector injector = (AnnotatedParamInjector) config.annotInjectors.getInjector(annotation.annotationType());
		if (injector != null) {
			return injector.resolve(context, annotation, paramName, parameterClass);
		}
		return null;
	}
}