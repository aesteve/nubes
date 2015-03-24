package io.vertx.mvc;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.apex.Router;
import io.vertx.mvc.annotations.AfterFilter;
import io.vertx.mvc.annotations.BeforeFilter;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Finalizer;
import io.vertx.mvc.annotations.Paginated;
import io.vertx.mvc.annotations.Route;
import io.vertx.mvc.annotations.Throttled;
import io.vertx.mvc.routing.MVCRoute;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;


public class VertxMVC {

	private Config config;
	private Vertx vertx;
	
	/**
	 * TODO check config
	 * @param vertx
	 */
	public VertxMVC(Vertx vertx, JsonObject json) {
		this.vertx = vertx;
		config = Config.fromJsonObject(json); 
	}
	
	public Router bootstrap(Router router) {
		List<MVCRoute> routes = extractRoutesFromControllers();
		routes.forEach(route -> {
			route.attachHandlersToRouter(router);
		});
		return router;
	}
	
	public Router bootstrap() {
		return bootstrap(Router.router(vertx));
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
		Route base = (Route)controller.getAnnotation(Route.class);
		Object instance;
		try {
			instance = controller.newInstance();
		} catch (InstantiationException | IllegalAccessException ie) {
			return routes;
		}
		String basePath = "";
		if (base != null) {
			basePath = base.path();
		}
		List<Method> beforeFilters = new ArrayList<Method>();
		List<Method> afterFilters = new ArrayList<Method>();
		Method finalizer = null;
		for (Method method : controller.getDeclaredMethods()) {
			if (method.getAnnotation(BeforeFilter.class) != null) {
				beforeFilters.add(method);
			} else if (method.getAnnotation(AfterFilter.class) != null) {
				afterFilters.add(method);
			} else if (method.getAnnotation(Route.class) != null) {
				Route path = (Route)method.getAnnotation(Route.class);
				boolean paginated = method.getAnnotation(Paginated.class) != null;
				MVCRoute route = new MVCRoute(instance, basePath + path.path(), path.method(), paginated);
				boolean throttled = method.getAnnotation(Throttled.class) != null;
				if (throttled && config.rateLimit != null) {
					route.setRateLimit(config.rateLimit);
				}
				route.setMainHandler(method);
				routes.add(route);
			} else if (method.getAnnotation(Finalizer.class) != null) {
				finalizer = method;
			}
		}
		for (MVCRoute route : routes) {
			route.addBeforeFilters(beforeFilters);
			route.addAfterFilters(afterFilters);
			if (finalizer != null) {
				route.setFinalizer(finalizer);
			}
		}
		if (controller.getSuperclass() != null) {
			extractFiltersFromController(routes, controller.getSuperclass());
		}
		return routes;
	}

	private void extractFiltersFromController(List<MVCRoute> routes, Class<?> controller) {
		List<Method> beforeFilters = new ArrayList<Method>();
		List<Method> afterFilters = new ArrayList<Method>();
		Method finalizer = null;
		for (Method method : controller.getDeclaredMethods()) {
			if (method.getAnnotation(BeforeFilter.class) != null) {
				beforeFilters.add(method);
			} else if (method.getAnnotation(AfterFilter.class) != null) {
				afterFilters.add(method);
			} else if (method.getAnnotation(Finalizer.class) != null) {
				finalizer = method;
			}
		}
		for (MVCRoute route : routes) {
			route.addBeforeFilters(beforeFilters);
			route.addAfterFilters(afterFilters);
			if (finalizer != null) {
				route.setFinalizer(finalizer);
			}
		}
		if (controller.getSuperclass() != null && !controller.getSuperclass().equals(Object.class)) {
			extractFiltersFromController(routes, controller.getSuperclass());
		}
	}
}
