package com.github.aesteve.vertx.nubes.reflections;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
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
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.annotations.routing.ServerRedirect;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessorRegistry;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.reflections.factories.AuthenticationFactory;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.routing.HttpMethodFactory;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;
import com.github.aesteve.vertx.nubes.services.ServiceRegistry;
import com.github.aesteve.vertx.nubes.utils.Filter;

public class RouteFactory {

	private Router router;
	private Config config;
	private AnnotationProcessorRegistry apRegistry;
	private Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers;
	private Map<Class<?>, Processor> typeProcessors;
	private TypedParamInjectorRegistry typedInjectors;
	private AnnotatedParamInjectorRegistry annotInjectors;
	private ServiceRegistry serviceRegistry;
	private RouteRegistry routeRegistry;
	private Map<Class<?>, Handler<RoutingContext>> paramHandlers;
	private AuthenticationFactory authFactory;

	public RouteFactory(Router router, Config config, Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers, Map<Class<?>, Processor> typeProcessors, AnnotationProcessorRegistry apRegistry, TypedParamInjectorRegistry typedInjectors, AnnotatedParamInjectorRegistry annotInjectors, ServiceRegistry serviceRegistry, Map<Class<?>, Handler<RoutingContext>> paramHandlers,
			AuthenticationFactory authFactory) {
		this.router = router;
		this.config = config;
		this.annotationHandlers = annotationHandlers;
		this.typeProcessors = typeProcessors;
		this.apRegistry = apRegistry;
		this.typedInjectors = typedInjectors;
		this.annotInjectors = annotInjectors;
		this.serviceRegistry = serviceRegistry;
		this.paramHandlers = paramHandlers;
		this.routeRegistry = new RouteRegistry();
		this.authFactory = authFactory;
	}

	public void createRoutes() {
		List<MVCRoute> routes = extractRoutesFromControllers();
		routes.forEach(route -> {
			route.attachHandlersToRouter(router, null, null);
		});
	}

	public List<MVCRoute> extractRoutesFromControllers() {
		List<MVCRoute> routes = new ArrayList<MVCRoute>();
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
		List<MVCRoute> routes = new ArrayList<MVCRoute>();
		Set<Processor> processors = new LinkedHashSet<Processor>();
		Controller base = (Controller) controller.getAnnotation(Controller.class);
		Object instance;
		try {
			instance = controller.newInstance();
			injectServicesIntoController(instance);
		} catch (InstantiationException | IllegalAccessException ie) {
			throw new RuntimeException("Could not instanciate controller : ", ie);
		}
		String basePath = "";
		if (base.value() != null) {
			basePath = base.value();
		}
		for (Method method : controller.getDeclaredMethods()) {
			if (method.getAnnotation(Path.class) != null) {
				Auth auth = method.getAnnotation(Auth.class);
				if (auth == null) {
					auth = controller.getAnnotation(Auth.class);
				}
				Set<Handler<RoutingContext>> paramsHandlers = new LinkedHashSet<Handler<RoutingContext>>();
				Class<?>[] parameterClasses = method.getParameterTypes();
				Annotation[][] parametersAnnotations = method.getParameterAnnotations();

				for (int i = 0; i < parameterClasses.length; i++) {
					Class<?> parameterClass = parameterClasses[i];
					Processor typeProcessor = typeProcessors.get(parameterClass);
					if (typeProcessor != null) {
						processors.add(typeProcessor);
					}
					Handler<RoutingContext> handler = paramHandlers.get(parameterClass);
					if (handler != null) {
						paramsHandlers.add(handler);
					}
					Annotation[] paramAnnotations = parametersAnnotations[i];
					if (paramAnnotations != null) {
						for (Annotation annotation : paramAnnotations) {
							Set<Handler<RoutingContext>> paramHandler = annotationHandlers.get(annotation.annotationType());
							if (paramHandler != null) {
								paramsHandlers.addAll(paramHandler);
							}
						}
					}
				}

				Path path = (Path) method.getAnnotation(Path.class);
				List<HttpMethod> httpMethods = HttpMethodFactory.fromAnnotatedMethod(method);
				for (HttpMethod httpMethod : httpMethods) {
					Handler<RoutingContext> authHandler = null;
					if (method.isAnnotationPresent(Auth.class) || controller.isAnnotationPresent(Auth.class)) {
						authHandler = authFactory.create(auth);
					}
					MVCRoute route = new MVCRoute(instance, basePath + path.value(), httpMethod, typedInjectors, annotInjectors, authHandler, config.vertx);
					for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
						Set<Handler<RoutingContext>> handler = annotationHandlers.get(methodAnnotation.annotationType());
						if (handler != null) {
							route.attachHandlers(handler);
						}
						AnnotationProcessor<?> annProcessor = apRegistry.getProcessor(methodAnnotation);
						if (annProcessor != null) {
							route.addProcessor(annProcessor);
						}
					}
					route.addProcessors(processors);
					route.attachHandlers(paramsHandlers);
					route.setMainHandler(method);
					routes.add(route);
					routeRegistry.register(controller, method, route);
					if (method.isAnnotationPresent(ServerRedirect.class)) {
						ServerRedirect redirect = method.getAnnotation(ServerRedirect.class);
						routeRegistry.bindRedirect(route, redirect);
					}
				}
			}
		}
		extractFiltersFromController(routes, controller);
		return routes;
	}

	private void extractFiltersFromController(List<MVCRoute> routes, Class<?> controller) {
		Set<Filter> beforeFilters = new TreeSet<Filter>();
		Set<Filter> afterFilters = new TreeSet<Filter>();
		for (Method method : controller.getDeclaredMethods()) {
			if (method.getAnnotation(BeforeFilter.class) != null) {
				beforeFilters.add(new Filter(method, method.getAnnotation(BeforeFilter.class)));
			} else if (method.getAnnotation(AfterFilter.class) != null) {
				afterFilters.add(new Filter(method, method.getAnnotation(AfterFilter.class)));
			}
		}
		Set<Processor> controllerProcessors = new LinkedHashSet<Processor>();
		for (Annotation annotation : controller.getDeclaredAnnotations()) {
			AnnotationProcessor<?> controllerProcessor = apRegistry.getProcessor(annotation);
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
			Object service = serviceRegistry.get(field);
			if (service != null) {
				field.setAccessible(true);
				field.set(instance, serviceRegistry.get(field));
			}
		}
	}
}
