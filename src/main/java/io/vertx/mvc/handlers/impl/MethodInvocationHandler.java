package io.vertx.mvc.handlers.impl;

import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.exceptions.BadRequestException;
import io.vertx.mvc.exceptions.HttpException;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjector;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import io.vertx.mvc.reflections.injectors.typed.ParamInjector;
import io.vertx.mvc.reflections.injectors.typed.TypedParamInjectorRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodInvocationHandler implements Handler<RoutingContext> {

	private static final Logger log = LoggerFactory.getLogger(MethodInvocationHandler.class);
	
	private Method method;
	private Object instance;
	private TypedParamInjectorRegistry typedInjectors;
	private AnnotatedParamInjectorRegistry annotInjectors;
	
	public MethodInvocationHandler(Object instance, 
			Method method, 
			TypedParamInjectorRegistry typedInjectors,
			AnnotatedParamInjectorRegistry annotInjectors) {
		this.method = method;
		this.instance = instance;
		this.typedInjectors = typedInjectors;
		this.annotInjectors = annotInjectors;
	}
	
	@Override
	public void handle(RoutingContext routingContext) {
		if (routingContext.response().ended()) {
			return;
		}
		try {
			List<Object> parameters = new ArrayList<Object>();
			Class<?>[] parameterClasses = method.getParameterTypes();
			Annotation[][] parametersAnnotations = method.getParameterAnnotations();
			for (int i = 0; i < parameterClasses.length; i++) {
				Object paramInstance = getParameterInstance(routingContext, parametersAnnotations[i], parameterClasses[i]);
				parameters.add(paramInstance);
			}
			method.invoke(instance, parameters.toArray());
		} catch (HttpException he) {
			routingContext.response().setStatusCode(he.getStatusCode());
			routingContext.response().setStatusMessage(he.getStatusMessage());
			routingContext.fail(he.getStatusCode());
		} catch (Throwable others) {
			log.error(others);
			routingContext.fail(others);
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass) throws BadRequestException {
		if (annotations.length == 0) { // rely on type
			ParamInjector<?> injector = typedInjectors.getInjector(parameterClass);
			if (injector == null) {
				return null;
			}
			return injector.resolve(context);
		}
		if (annotations.length > 1) {
			throw new IllegalArgumentException("Every parameter should only have ONE annotation");
		}
		Annotation annotation = annotations[0]; // rely on annotation
		AnnotatedParamInjector injector = (AnnotatedParamInjector) annotInjectors.getInjector(annotation.annotationType());
		if (injector != null) {
			return injector.resolve(context, annotation, parameterClass);
		}
		return null;
	}
}