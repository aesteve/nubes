package io.vertx.mvc;

import io.vertx.core.Vertx;
import io.vertx.ext.apex.Router;
import io.vertx.mvc.annotations.AfterFilter;
import io.vertx.mvc.annotations.BeforeFilter;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Finalizer;
import io.vertx.mvc.annotations.Route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;


public class VertxMVC {
	
	public static Router bootstrap(Router router, List<String> controllerPackages) {
		List<MVCRoute> routes = extractRoutesFromControllers(controllerPackages);
		routes.forEach(route -> {
			route.beforeFilters().forEach(filter -> {
				setHandler(router, route, filter);
			});
			setHandler(router, route, route.mainHandler());
			route.afterFilters().forEach(filter -> {
				setHandler(router, route, filter);
			});
			if (route.finalizer() != null) {
				setHandler(router, route, route.finalizer());
			}
		});
		return router;
	}

	public static Router bootstrap(Router router, String controllerPackage) {
		List<String> controllerPackages = new ArrayList<String>();
		controllerPackages.add(controllerPackage);
		return bootstrap(router, controllerPackages);
	}
	
	public static Router bootstrap(Vertx vertx, List<String> controllerPackages) {
		return bootstrap(Router.router(vertx), controllerPackages);
	}

	public static Router bootstrap(Vertx vertx, String controllerPackage) {
		return bootstrap(Router.router(vertx), controllerPackage);
	}
	
	public static List<MVCRoute> extractRoutesFromControllers(String controllerPackages) {
		List<String> list = new ArrayList<String>();
		list.add(controllerPackages);
		return extractRoutesFromControllers(list);
	}
	
	public static List<MVCRoute> extractRoutesFromControllers(List<String> controllerPackages) {
		List<MVCRoute> routes = new ArrayList<MVCRoute>();
		controllerPackages.forEach(controllerPackage -> {
			Reflections reflections = new Reflections(controllerPackage);
			Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
			controllers.forEach(controller -> {
				routes.addAll(extractRoutesFromController(controller));
			});
		});
		return routes;
	}
	
	private static List<MVCRoute> extractRoutesFromController(Class<?> controller) {
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
				MVCRoute route = new MVCRoute(instance, basePath + path.path(), path.method());
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
	
	private static void extractFiltersFromController(List<MVCRoute> routes, Class<?> controller) {
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
	
	private static void setHandler(Router router, MVCRoute route, Method method) {
		router.route(route.method(), route.path()).handler(routingContext -> {
			if (routingContext.response().ended()) {
				return; // ignore
			}
			try {
				method.invoke(route.controller(), routingContext);
			} catch(Exception e) {
				e.printStackTrace();
				routingContext.fail(e);
			}
		});				
	}
}
