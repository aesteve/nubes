package com.github.aesteve.vertx.nubes.reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.annotations.routing.ServerRedirect;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;

/**
 * FIXME : two methods could have the same name but not the same declaration...
 */
public class RouteRegistry {

    private Map<String, MVCRoute> discovered;
    private Map<String, MVCRoute> waiting;

    public RouteRegistry() {
        discovered = new HashMap<String, MVCRoute>();
        waiting = new HashMap<String, MVCRoute>();
    }

    public void register(Class<?> controller, Method handler, MVCRoute route) {
        String key = buildKey(controller, handler);
        discovered.put(key, route);
        if (waiting.get(key) != null) {
            waiting.get(key).redirectTo(route);
        }
    }

    public void bindRedirect(MVCRoute route, ServerRedirect redirect) {
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

    public MVCRoute get(ServerRedirect annotation) {
        return discovered.get(buildKey(annotation));
    }

    private String buildKey(Class<?> controller, String methodName) {
        return controller.getName() + "::" + methodName;
    }

    private String buildKey(Class<?> controller, Method handler) {
        return buildKey(controller, handler.getName());
    }

    private String buildKey(ServerRedirect annotation) {
        return buildKey(annotation.controller(), annotation.action());
    }
}
