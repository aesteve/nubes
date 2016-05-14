package com.github.aesteve.vertx.nubes.reflections;

import com.github.aesteve.vertx.nubes.annotations.routing.Forward;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouteRegistry {

  private static final String SEP = "::";

  private final Map<String, MVCRoute> discovered;
  private final Map<String, MVCRoute> waiting;

  RouteRegistry() {
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

  public boolean exists(Class<?> controller, Method handler) {
    return discovered.get(buildKey(controller, handler)) != null;
  }

  private MVCRoute get(Forward annotation) {
    return discovered.get(buildKey(annotation));
  }

  private static String buildKey(Class<?> controller, Method handler) {
    // Either we're relying on user to name his methods right or have to put the parameter in the "Forward" annotation
    // in this case : params.forEach(-> add to StringJoiner)
    return controller.getName() + SEP + handler.getName();
  }

  private static String buildKey(Forward annotation) {
    return annotation.controller().getName() + SEP + annotation.action();
  }
}
