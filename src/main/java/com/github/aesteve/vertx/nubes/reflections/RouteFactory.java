package com.github.aesteve.vertx.nubes.reflections;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.filters.After;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.Before;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.annotations.routing.Disabled;
import com.github.aesteve.vertx.nubes.annotations.routing.Forward;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.reflections.factories.AuthenticationFactory;
import com.github.aesteve.vertx.nubes.routing.HttpMethodFactory;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;
import com.github.aesteve.vertx.nubes.utils.Filter;

public class RouteFactory {

	private static final Logger log = LoggerFactory.getLogger(RouteFactory.class);

	private Router router;
	private Config config;
	private RouteRegistry routeRegistry;
	private AuthenticationFactory authFactory;

	public RouteFactory(Router router, Config config) {
		this.router = router;
		this.config = config;
		this.routeRegistry = new RouteRegistry();
		this.authFactory = new AuthenticationFactory(config);
	}

	public void createRoutes() {
		List<MVCRoute> routes = extractRoutesFromControllers();
		routes.stream().filter(route -> {
			return route.isEnabled();
		}).forEach(route -> {
			route.attachHandlersToRouter(router, null, null);
		});
	}

	public List<MVCRoute> extractRoutesFromControllers() {
		List<MVCRoute> routes = new ArrayList<>();
		config.controllerPackages.forEach(controllerPackage -> {
			Reflections reflections = new Reflections(controllerPackage);
			Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
			controllers.forEach(controller -> {
				routes.addAll(extractRoutesFromController(controller));
			});
		});
		return routes;
	}

	private List<MVCRoute> extractRoutesFromController(Class<?> controller) {
		List<MVCRoute> routes = new ArrayList<>();
		Set<Processor> processors = new LinkedHashSet<>();
		Controller base = (Controller) controller.getAnnotation(Controller.class);
		Object instance;
		try {
			instance = controller.newInstance();
			injectServicesIntoController(instance);
		} catch (InstantiationException | IllegalAccessException ie) {
			throw new RuntimeException("Could not instanciate controller : ", ie);
		}
		String trBasePath = "";
		if (base.value() != null) {
			trBasePath = base.value();
		}
		final String basePath = trBasePath; // java 8...
		for (Method method : controller.getDeclaredMethods()) {
			if (HttpMethodFactory.isRouteMethod(method)) {
				Auth trAuth = method.getAnnotation(Auth.class);
				if (trAuth == null) {
					trAuth = controller.getAnnotation(Auth.class);
				}
				final Auth auth = trAuth; // java 8...
				Set<Handler<RoutingContext>> paramsHandlers = new LinkedHashSet<>();
				Class<?>[] parameterClasses = method.getParameterTypes();
				Annotation[][] parametersAnnotations = method.getParameterAnnotations();

				for (int i = 0; i < parameterClasses.length; i++) {
					Class<?> parameterClass = parameterClasses[i];
					Processor typeProcessor = config.typeProcessors.get(parameterClass);
					if (typeProcessor != null) {
						processors.add(typeProcessor);
					}
					Handler<RoutingContext> handler = config.paramHandlers.get(parameterClass);
					if (handler != null) {
						paramsHandlers.add(handler);
					}
					Annotation[] paramAnnotations = parametersAnnotations[i];
					if (paramAnnotations != null) {
						for (Annotation annotation : paramAnnotations) {
							Set<Handler<RoutingContext>> paramHandler = config.annotationHandlers.get(annotation.annotationType());
							if (paramHandler != null) {
								paramsHandlers.addAll(paramHandler);
							}
						}
					}
				}

				Map<HttpMethod, String> httpMethods = HttpMethodFactory.fromAnnotatedMethod(method);
				httpMethods.forEach((httpMethod, path) -> {
					Handler<RoutingContext> authHandler = null;
					if (method.isAnnotationPresent(Auth.class) || controller.isAnnotationPresent(Auth.class)) {
						authHandler = authFactory.create(auth);
					}
					boolean disabled = method.isAnnotationPresent(Disabled.class) || controller.isAnnotationPresent(Disabled.class);
					MVCRoute route = new MVCRoute(instance, basePath + path, httpMethod, config, authHandler, disabled);
					for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
						Set<Handler<RoutingContext>> handler = config.annotationHandlers.get(methodAnnotation.annotationType());
						if (handler != null) {
							route.attachHandlers(handler);
						}
						AnnotationProcessor<?> annProcessor = config.apRegistry.getProcessor(methodAnnotation);
						if (annProcessor != null) {
							route.addProcessor(annProcessor);
						}
					}
					Before before = method.getAnnotation(Before.class);
					After after = method.getAnnotation(After.class);
					if (before != null) {
						Handler<RoutingContext> beforeHandler = config.aopHandlerRegistry.get(before.name());
						if (beforeHandler == null) {
							log.warn("The interceptor with name" + (before.name()) + " could not be found");
						} else {
							route.attachInterceptor(beforeHandler, true);
						}
					}
					if (after != null) {
						Handler<RoutingContext> afterHandler = config.aopHandlerRegistry.get(after.name());
						if (afterHandler == null) {
							log.warn("The interceptor with name" + (after.name()) + " could not be found");
						} else {
							route.attachInterceptor(afterHandler, false);
						}
					}
					route.addProcessors(processors);
					route.attachHandlers(paramsHandlers);
					route.setMainHandler(method);
					routes.add(route);
					routeRegistry.register(controller, method, route);
					if (method.isAnnotationPresent(Forward.class)) {
						Forward redirect = method.getAnnotation(Forward.class);
						routeRegistry.bindRedirect(route, redirect);
					}
				});
			}
		}
		extractFiltersFromController(routes, controller);
		return routes;
	}

	private void extractFiltersFromController(List<MVCRoute> routes, Class<?> controller) {
		Set<Filter> beforeFilters = new TreeSet<>();
		Set<Filter> afterFilters = new TreeSet<>();
		for (Method method : controller.getDeclaredMethods()) {
			if (method.getAnnotation(BeforeFilter.class) != null) {
				beforeFilters.add(new Filter(method, method.getAnnotation(BeforeFilter.class)));
			} else if (method.getAnnotation(AfterFilter.class) != null) {
				afterFilters.add(new Filter(method, method.getAnnotation(AfterFilter.class)));
			}
		}
		Set<Processor> controllerProcessors = new LinkedHashSet<>();
		for (Annotation annotation : controller.getDeclaredAnnotations()) {
			AnnotationProcessor<?> controllerProcessor = config.apRegistry.getProcessor(annotation);
			if (controllerProcessor != null) {
				controllerProcessors.add(controllerProcessor);
			}
		}
		for (MVCRoute route : routes) {
			route.addProcessorsFirst(controllerProcessors);
			route.addBeforeFilters(beforeFilters);
			route.addAfterFilters(afterFilters);
		}
		if (controller.getSuperclass() != null && !controller.getSuperclass().equals(Object.class)) {
			extractFiltersFromController(routes, controller.getSuperclass());
		}
	}

	private void injectServicesIntoController(Object instance) throws IllegalAccessException {
		for (Field field : instance.getClass().getDeclaredFields()) {
			Object service = config.serviceRegistry.get(field);
			if (service != null) {
				field.setAccessible(true);
				field.set(instance, config.serviceRegistry.get(field));
			}
		}
	}
}
