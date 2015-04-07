package io.vertx.mvc.routing;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.handlers.AnnotationProcessor;
import io.vertx.mvc.handlers.Processor;
import io.vertx.mvc.handlers.impl.MethodInvocationHandler;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import io.vertx.mvc.reflections.injectors.typed.TypedParamInjectorRegistry;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MVCRoute {

	private final String path;
	private final HttpMethod httpMethod;
	private final Object instance;
	private List<Method> beforeFilters;
	private List<Method> afterFilters;
	private Method mainHandler;
	private Set<Handler<RoutingContext>> handlers;
	private Set<Processor> processors;
	private TypedParamInjectorRegistry typedInjectors;
	private AnnotatedParamInjectorRegistry annotatedInjectors;

	public MVCRoute(Object instance, 
			String path, 
			HttpMethod method, 
			TypedParamInjectorRegistry typedInjectors,
			AnnotatedParamInjectorRegistry annotatedInjectors) {
		this.instance = instance;
		this.path = path;
		this.httpMethod = method;
		this.beforeFilters = new ArrayList<Method>();
		this.afterFilters = new ArrayList<Method>();
		this.handlers = new LinkedHashSet<Handler<RoutingContext>>();
		this.processors = new LinkedHashSet<Processor>();
		this.typedInjectors = typedInjectors;
		this.annotatedInjectors = annotatedInjectors;
	}
	
	public void addProcessor(Processor processor) {
		processors.add(processor);
	}
	
	public void addProcessors(Set<Processor> processors) {
		this.processors.addAll(processors);
	}
	
	public void addProcessorsFirst(Set<Processor> processors) {
		processors.addAll(this.processors);
		this.processors = processors;
	}
	
	public void attachHandler(Handler<RoutingContext> handler) {
		handlers.add(handler);
	}
	
	public void attachHandlers(Set<Handler<RoutingContext>> newHandlers) {
		handlers.addAll(newHandlers);
	}

	public void setMainHandler(Method mainHandler) {
		this.mainHandler = mainHandler;
	}

	public void addBeforeFilters(List<Method> beforeFilters) {
		this.beforeFilters.addAll(beforeFilters);
	}

	public void addAfterFilters(List<Method> afterFilters) {
		this.afterFilters.addAll(afterFilters);
	}

	public String path() {
		return path;
	}

	public HttpMethod method() {
		return httpMethod;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void attachHandlersToRouter(Router router) {
		processors.forEach(processor -> {
			router.route(httpMethod, path).handler(context -> {
				if (processor instanceof AnnotationProcessor) {
					AnnotationProcessor realProcessor = (AnnotationProcessor)processor;
					Annotation annotation = mainHandler.getAnnotation(realProcessor.getAnnotationType());
					if (annotation == null){
						annotation = instance.getClass().getAnnotation(realProcessor.getAnnotationType());
					}
					realProcessor.init(context, annotation);
				}
				processor.preHandle(context);	
			});
		});
		handlers.forEach(handler -> {
			router.route(httpMethod, path).handler(handler);
		});
		beforeFilters.forEach(filter -> {
			setHandler(router, filter);
		});
		setHandler(router, mainHandler);
		afterFilters.forEach(filter -> {
			setHandler(router, filter);
		});
		processors.forEach(processor -> {
			router.route(httpMethod, path).handler(processor::postHandle);
		});
	}
	
	private void setHandler(Router router, Method method) {
		router.route(httpMethod, path).handler(new MethodInvocationHandler(instance, method, typedInjectors, annotatedInjectors));
	}

	@Override
	public String toString() {
		return "Route : " + httpMethod.toString() + " " + path();
	}	
}
