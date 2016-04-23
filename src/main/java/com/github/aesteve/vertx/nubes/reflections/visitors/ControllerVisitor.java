package com.github.aesteve.vertx.nubes.reflections.visitors;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.reflections.Filter;
import com.github.aesteve.vertx.nubes.reflections.RouteRegistry;
import com.github.aesteve.vertx.nubes.reflections.factories.AuthenticationFactory;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;
import io.vertx.core.VertxException;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

public class ControllerVisitor<T> extends BaseVisitor<T> {

	private final Method[] methods;
	final String basePath;
	final AuthenticationFactory authFactory;
	final RouteRegistry routeRegistry;
	final Map<Class<? extends Annotation>, BiConsumer<RoutingContext, ?>> returnHandlers;
	final Set<Processor> processors;
	private final Set<Filter> beforeFilters;
	private final Set<Filter> afterFilters;


	public ControllerVisitor(Class<T> controllerClass, Config config, Router router, AuthenticationFactory authFactory, RouteRegistry routeRegistry, Map<Class<? extends Annotation>, BiConsumer<RoutingContext, ?>> returnHandlers) {
		super(controllerClass, config, router);
		this.routeRegistry = routeRegistry;
		this.returnHandlers = returnHandlers;
		methods = controllerClass.getDeclaredMethods();
		Controller base = clazz.getAnnotation(Controller.class);
		basePath = base.value();
		this.authFactory = authFactory;
		processors = new LinkedHashSet<>();
		beforeFilters = new TreeSet<>();
		afterFilters = new TreeSet<>();
	}

	public List<MVCRoute> visit() throws IllegalAccessException, InstantiationException {
		instance = clazz.newInstance();
		List<MVCRoute> routes = new ArrayList<>();
		try {
			injectServices();
		} catch (IllegalAccessException iae) {
			throw new VertxException(iae);
		}
		extractFilters();
		for (Method method : methods) {
			MethodVisitor<T> visitor = new MethodVisitor<>(this, method);
			routes.addAll(visitor.visit());
		}
		for (MVCRoute route : routes) {
			route.addProcessorsFirst(processors);
			route.addBeforeFilters(beforeFilters);
			route.addAfterFilters(afterFilters);
		}
		return routes;
	}

	private void extractFilters() {
		if (hasSuperClass()) {
			ControllerVisitor<?> superClass = new ControllerVisitor<>(clazz.getSuperclass(), config, router, authFactory, routeRegistry, returnHandlers);
			superClass.extractFilters();
			beforeFilters.addAll(superClass.beforeFilters);
			afterFilters.addAll(superClass.afterFilters);
			processors.addAll(superClass.processors);
		}
		for (Method method : methods) {
			BeforeFilter beforeAnnot = method.getAnnotation(BeforeFilter.class);
			AfterFilter afterAnnot = method.getAnnotation(AfterFilter.class);
			if (beforeAnnot != null) {
				beforeFilters.add(new Filter(method, beforeAnnot));
			} else if (afterAnnot != null) {
				afterFilters.add(new Filter(method, afterAnnot));
			}
		}
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			AnnotationProcessor<?> controllerProcessor = config.getAnnotationProcessor(annotation);
			if (controllerProcessor != null) {
				processors.add(controllerProcessor);
			}
		}
	}

	private boolean hasSuperClass() {
		return clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class);
	}

}
