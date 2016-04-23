package com.github.aesteve.vertx.nubes.reflections.visitors;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.filters.After;
import com.github.aesteve.vertx.nubes.annotations.filters.Before;
import com.github.aesteve.vertx.nubes.annotations.routing.Disabled;
import com.github.aesteve.vertx.nubes.annotations.routing.Forward;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.reflections.RouteRegistry;
import com.github.aesteve.vertx.nubes.reflections.factories.AuthenticationFactory;
import com.github.aesteve.vertx.nubes.routing.HttpMethodFactory;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

class MethodVisitor<T> {

	private final static Logger LOG = LoggerFactory.getLogger(MethodVisitor.class);

	private final Class<T> controller;
	private final Method method;
	private final Config config;
	private final T instance;
	private final String basePath;
	private final Auth auth;
	private final AuthenticationFactory authFactory;
	private final RouteRegistry routeRegistry;
	private final Map<Class<? extends Annotation>, BiConsumer<RoutingContext, ?>> returnHandlers;
	private final Set<Processor> processors;
	private final Set<Handler<RoutingContext>> paramsHandlers;
	private final List<MVCRoute> routes;
	private boolean usesSession;


	MethodVisitor(ControllerVisitor<T> parent, Method method) {
		this.method = method;
		controller = parent.clazz;
		config = parent.config;
		instance = parent.instance;
		basePath = parent.basePath;
		auth = method.getAnnotation(Auth.class) == null ? controller.getAnnotation(Auth.class) : method.getAnnotation(Auth.class);
		authFactory = parent.authFactory;
		routeRegistry = parent.routeRegistry;
		returnHandlers = parent.returnHandlers;
		processors = parent.processors;
		paramsHandlers = new LinkedHashSet<>();
		routes = new ArrayList<>();
	}

	List<MVCRoute> visit() {
		if (!HttpMethodFactory.isRouteMethod(method)) {
			return routes;
		}
		createParamsHandlers();
		Map<HttpMethod, String> httpMethods = HttpMethodFactory.fromAnnotatedMethod(method);
		routes.addAll(httpMethods.entrySet().stream().map(this::createHandlers).collect(Collectors.toList()));
		return routes;
	}

	private MVCRoute createHandlers(Map.Entry<HttpMethod, String> entry) {
		HttpMethod httpMethod = entry.getKey();
		String path = entry.getValue();
		MVCRoute route = createRoute(httpMethod, path);
		handleMethodAnnotations(route);
		createAopProcessors(route);
		route.addProcessors(processors);
		route.attachHandlers(paramsHandlers);
		route.setMainHandler(method);
		routeRegistry.register(controller, method, route);
		if (method.isAnnotationPresent(Forward.class)) {
			Forward redirect = method.getAnnotation(Forward.class);
			routeRegistry.bindRedirect(route, redirect);
		}
		return route;
	}

	private void createAopProcessors(MVCRoute route) {
		Before before = method.getAnnotation(Before.class);
		After after = method.getAnnotation(After.class);
		if (before != null) {
			Handler<RoutingContext> beforeHandler = config.getAopHandler(before.name());
			if (beforeHandler == null) {
				LOG.warn("The interceptor with name" + (before.name()) + " could not be found");
			} else {
				route.attachInterceptor(beforeHandler, true);
			}
		}
		if (after != null) {
			Handler<RoutingContext> afterHandler = config.getAopHandler(after.name());
			if (afterHandler == null) {
				LOG.warn("The interceptor with name" + (after.name()) + " could not be found");
			} else {
				route.attachInterceptor(afterHandler, false);
			}
		}
	}

	private void handleMethodAnnotations(MVCRoute route) {
		for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
			Class<? extends Annotation> annotClass = methodAnnotation.annotationType();
			Set<Handler<RoutingContext>> handler = config.getAnnotationHandler(annotClass);
			if (handler != null) {
				route.attachHandlers(handler);
			}
			AnnotationProcessor<?> annProcessor = config.getAnnotationProcessor(methodAnnotation);
			if (annProcessor != null) {
				route.addProcessor(annProcessor);
			}
			BiConsumer<RoutingContext, ?> returnHandler = returnHandlers.get(annotClass);
			if (returnHandler != null) {
				route.attachReturnHandler(returnHandler);
			}
		}
	}

	private MVCRoute createRoute(HttpMethod httpMethod, String path) {
		Handler<RoutingContext> authHandler = null;
		String redirectURL = null;
		if (auth != null) {
			authHandler = authFactory.create(auth);
			if (AuthMethod.REDIRECT.equals(auth.method())) {
				redirectURL = auth.redirectURL();
			}
		}
		boolean disabled = method.isAnnotationPresent(Disabled.class) || controller.isAnnotationPresent(Disabled.class);
		MVCRoute route = new MVCRoute(instance, basePath + path, httpMethod, config, authHandler, disabled, usesSession);
		route.setLoginRedirect(redirectURL);
		return route;
	}

	private void createParamsHandlers() {
		for (Parameter p : method.getParameters()) {
			Class<?> parameterClass = p.getType();
			if (Session.class.isAssignableFrom(parameterClass)) {
				usesSession = true;
			}
			Processor typeProcessor = config.getTypeProcessor(parameterClass);
			if (typeProcessor != null) {
				processors.add(typeProcessor);
			}
			Handler<RoutingContext> handler = config.getParamHandler(parameterClass);
			if (handler != null) {
				paramsHandlers.add(handler);
			}
			Annotation[] paramAnnotations = p.getAnnotations();
			if (paramAnnotations != null) {
				for (Annotation annotation : paramAnnotations) {
					Set<Handler<RoutingContext>> paramHandler = config.getAnnotationHandler(annotation.annotationType());
					if (paramHandler != null) {
						paramsHandlers.addAll(paramHandler);
					}
				}
			}
		}
	}

}
