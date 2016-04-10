package com.github.aesteve.vertx.nubes.reflections;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import io.vertx.ext.web.Session;
import org.reflections.Reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.filters.After;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.Before;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.annotations.routing.Disabled;
import com.github.aesteve.vertx.nubes.annotations.routing.Forward;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import com.github.aesteve.vertx.nubes.context.FileResolver;
import com.github.aesteve.vertx.nubes.context.ViewResolver;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.reflections.factories.AuthenticationFactory;
import com.github.aesteve.vertx.nubes.routing.HttpMethodFactory;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;

public class RouteFactory extends AbstractInjectionFactory implements HandlerFactory {

	private static final Logger LOG = LoggerFactory.getLogger(RouteFactory.class);

	private final Router router;
	private final RouteRegistry routeRegistry;
	private final AuthenticationFactory authFactory;
	private Map<Class<? extends Annotation>, BiConsumer<RoutingContext, ?>> returnHandlers;

	public RouteFactory(Router router, Config config) {
		this.router = router;
		this.config = config;
		routeRegistry = new RouteRegistry();
		authFactory = new AuthenticationFactory(config);
		createReturnHandlers();
	}

	private void createReturnHandlers() {
		returnHandlers = new HashMap<>();
		returnHandlers.put(View.class, (context, res) -> {
			if (res instanceof String) {
				ViewResolver.resolve(context, (String) res);
			} else if (res instanceof Map) {
				context.data().putAll((Map)res);
			}
		});
		returnHandlers.put(File.class, (context, res) -> FileResolver.resolve(context, (String) res));
	}

	public void createHandlers() {
		List<MVCRoute> routes = extractRoutesFromControllers();
		routes.stream().filter(MVCRoute::isEnabled).forEach(route -> route.attachHandlersToRouter(router, null, null));
	}

	private List<MVCRoute> extractRoutesFromControllers() {
		List<MVCRoute> routes = new ArrayList<>();
		config.controllerPackages.forEach(controllerPackage -> {
			Reflections reflections = new Reflections(controllerPackage);
			Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
			controllers.forEach(controller -> routes.addAll(extractRoutesFromController(controller)));
		});
		return routes;
	}

	private List<MVCRoute> extractRoutesFromController(Class<?> controller) {
		List<MVCRoute> routes = new ArrayList<>();
		Set<Processor> processors = new LinkedHashSet<>();
		Controller base = controller.getAnnotation(Controller.class);
		Object instance;
		try {
			instance = controller.newInstance();
			injectServicesIntoController(router, instance);
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
				boolean usesSession = false;
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
					if (Session.class.isAssignableFrom(parameterClass)) {
						usesSession = true;
					}
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
				final boolean doesUseSession = usesSession;
				httpMethods.forEach((httpMethod, path) -> {
					Handler<RoutingContext> authHandler = null;
					String redirectURL = null;
					if (auth != null) {
						authHandler = authFactory.create(auth);
						if (AuthMethod.REDIRECT.equals(auth.method())) {
							redirectURL = auth.redirectURL();
						}
					}
					boolean disabled = method.isAnnotationPresent(Disabled.class) || controller.isAnnotationPresent(Disabled.class);
					MVCRoute route = new MVCRoute(instance, basePath + path, httpMethod, config, authHandler, disabled, doesUseSession);
					route.setLoginRedirect(redirectURL);
					for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
						Class<? extends Annotation> annotClass = methodAnnotation.annotationType();
						Set<Handler<RoutingContext>> handler = config.annotationHandlers.get(annotClass);
						if (handler != null) {
							route.attachHandlers(handler);
						}
						AnnotationProcessor<?> annProcessor = config.apRegistry.getProcessor(methodAnnotation);
						if (annProcessor != null) {
							route.addProcessor(annProcessor);
						}
						BiConsumer<RoutingContext, ?> returnHandler = returnHandlers.get(annotClass);
						if (returnHandler != null) {
							route.attachReturnHandler(returnHandler);
						}
					}
					Before before = method.getAnnotation(Before.class);
					After after = method.getAnnotation(After.class);
					if (before != null) {
						Handler<RoutingContext> beforeHandler = config.aopHandlerRegistry.get(before.name());
						if (beforeHandler == null) {
							LOG.warn("The interceptor with name" + (before.name()) + " could not be found");
						} else {
							route.attachInterceptor(beforeHandler, true);
						}
					}
					if (after != null) {
						Handler<RoutingContext> afterHandler = config.aopHandlerRegistry.get(after.name());
						if (afterHandler == null) {
							LOG.warn("The interceptor with name" + (after.name()) + " could not be found");
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
}
