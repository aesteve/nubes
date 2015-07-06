package com.github.aesteve.vertx.nubes.reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.annotations.routing.Forward;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;

/**
 * FIXME : two methods could have the same name but not the same declaration...
 */
public class RouteRegistry {

	private Map<String, MVCRoute> discovered;
	private Map<String, MVCRoute> waiting;

	public RouteRegistry() {
		discovered = new HashMap<>();
		waiting = new HashMap<>();
	}

	public void register(Class<?> controller, Method handler, MVCRoute route) {
		String key = buildKey(controller, handler);
		discovered.put(key, route);
		if (waiting.get(key) != null) {
			waiting.get(key).redirectTo(route);
		}
	}

	public void bindRedirect(MVCRoute route, Forward redirect) {
		MVCRoute redirectRoute = get(redirect);
		if (redirectRoute != null) {
			route.redirectTo(redirectRoute);
		} else {
			waiting.put(buildKey(redirect), route);
		}
	}

	public MVCRoute get(Class<?> controller, Method handler) {
		return discovered.get(buildKey(controller, handler));
	}

	public MVCRoute get(Class<?> controller, String methodName) {
		return discovered.get(buildKey(controller, methodName));
	}

	public boolean exists(Class<?> controller, Method handler) {
		return discovered.get(buildKey(controller, handler)) != null;
	}

	public MVCRoute get(Forward annotation) {
		return discovered.get(buildKey(annotation));
	}

	private static String buildKey(Class<?> controller, String methodName) {
		return controller.getName() + "::" + methodName;
	}

	private static String buildKey(Class<?> controller, Method handler) {
		return buildKey(controller, handler.getName());
	}

	private static String buildKey(Forward annotation) {
		return buildKey(annotation.controller(), annotation.action());
	}
}
