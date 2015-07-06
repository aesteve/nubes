package com.github.aesteve.vertx.nubes.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

public abstract class AbstractMethodInvocationHandler<T> implements Handler<RoutingContext> {

	protected Method method;
	protected Object instance;
	protected Config config;
	protected Class<?>[] parameterClasses;
	protected Annotation[][] parametersAnnotations;
	protected boolean usesRoutingContext;
	protected boolean hasNext;
	protected BiConsumer<RoutingContext, T> returnHandler;
	protected boolean returnsSomething;

	public AbstractMethodInvocationHandler(Object instance, Method method, Config config, boolean hasNext, BiConsumer<RoutingContext, T> returnHandler) {
		this.method = method;
		returnsSomething = !method.getReturnType().equals(Void.TYPE);
		this.hasNext = hasNext;
		parameterClasses = method.getParameterTypes();
		for (Class<?> parameterClass : parameterClasses) {
			if (parameterClass.equals(RoutingContext.class)) {
				usesRoutingContext = true;
				break;
			}
		}
		parametersAnnotations = method.getParameterAnnotations();
		this.config = config;
		this.instance = instance;
		this.returnHandler = returnHandler;
	}

	@Override
	abstract public void handle(RoutingContext routingContext);

	protected Object[] getParameters(RoutingContext routingContext) {
		List<Object> parameters = new ArrayList<>();
		for (int i = 0; i < parameterClasses.length; i++) {
			Object paramInstance = getParameterInstance(routingContext, parametersAnnotations[i], parameterClasses[i]);
			parameters.add(paramInstance);
		}
		return parameters.toArray();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass) {
		if (annotations.length == 0) { // rely on type
			ParamInjector<?> injector = config.typeInjectors.getInjector(parameterClass);
			if (injector == null) {
				return null;
			}
			try {
				return injector.resolve(context);
			} catch (Exception e) {
				DefaultErrorHandler.badRequest(context, "Invalid parameter for : " + parameterClass);
			}
		}
		if (annotations.length > 1) {
			throw new IllegalArgumentException("Every parameter should only have ONE annotation");
		}
		Annotation annotation = annotations[0]; // rely on annotation
		AnnotatedParamInjector injector = (AnnotatedParamInjector) config.annotInjectors.getInjector(annotation.annotationType());
		if (injector != null) {
			try {
				return injector.resolve(context, annotation, parameterClass);
			} catch (Exception e) {
				DefaultErrorHandler.badRequest(context, "Invalid parameter value for : " + parameterClass);
			}
		}
		return null;
	}
}