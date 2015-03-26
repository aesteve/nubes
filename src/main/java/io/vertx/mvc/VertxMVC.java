package io.vertx.mvc;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.handler.CookieHandler;
import io.vertx.mvc.annotations.AfterFilter;
import io.vertx.mvc.annotations.BeforeFilter;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.UsesCookies;
import io.vertx.mvc.annotations.Finalizer;
import io.vertx.mvc.annotations.Paginated;
import io.vertx.mvc.annotations.Path;
import io.vertx.mvc.annotations.Throttled;
import io.vertx.mvc.context.ClientAccesses;
import io.vertx.mvc.context.RateLimit;
import io.vertx.mvc.routing.HttpMethodFactory;
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
     * 
     * @param vertx
     */
    public VertxMVC(Vertx vertx, JsonObject json) {
        this.vertx = vertx;
        config = Config.fromJsonObject(json);
    }

    public Router bootstrap(Router router) {
        List<MVCRoute> routes = extractRoutesFromControllers();
        CookieHandler cookieHandler = CookieHandler.create();
        routes.forEach(route -> {
            if (route.usesCookies()) {
                route.attachHandlersToRouter(router, cookieHandler);
            } else {
                route.attachHandlersToRouter(router);
            }

        });
        periodicallyCleanHistoryMap();
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
        Path base = (Path) controller.getAnnotation(Path.class);
        Object instance;
        try {
            instance = controller.newInstance();
        } catch (InstantiationException | IllegalAccessException ie) {
            return routes;
        }
        String basePath = "";
        if (base != null) {
            basePath = base.value();
        }
        boolean controllerUsesCookies = controller.getAnnotation(UsesCookies.class) != null;
        List<Method> beforeFilters = new ArrayList<Method>();
        List<Method> afterFilters = new ArrayList<Method>();
        Method finalizer = null;
        for (Method method : controller.getDeclaredMethods()) {
            if (method.getAnnotation(BeforeFilter.class) != null) {
                beforeFilters.add(method);
            } else if (method.getAnnotation(AfterFilter.class) != null) {
                afterFilters.add(method);
            } else if (method.getAnnotation(Path.class) != null) {
                Path path = (Path) method.getAnnotation(Path.class);
                boolean paginated = method.getAnnotation(Paginated.class) != null;
                List<HttpMethod> httpMethods = HttpMethodFactory.fromAnnotatedMethod(method);
                for (HttpMethod httpMethod : httpMethods) {
                    MVCRoute route = new MVCRoute(instance, basePath + path.value(), httpMethod, paginated);
                    boolean throttled = method.getAnnotation(Throttled.class) != null;
                    boolean usesCookies = method.getAnnotation(UsesCookies.class) != null;
                    if (throttled && config.rateLimit != null) {
                        route.setRateLimit(config.rateLimit);
                    }
                    if (usesCookies || controllerUsesCookies) {
                        route.usesCookies(true);
                    }
                    route.setMainHandler(method);
                    routes.add(route);
                }

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

    private void periodicallyCleanHistoryMap() {
        vertx.setPeriodic(60000, timerId -> {
            LocalMap<Object, Object> rateLimitations = vertx.sharedData().getLocalMap("mvc.rateLimitation");
            if (rateLimitations == null) {
                return;
            }
            List<String> clientIpsToRemove = new ArrayList<String>();
            RateLimit rateLimit = config.rateLimit;
            for (Object key : rateLimitations.keySet()) {
                String clientIp = (String) key;
                ClientAccesses accesses = (ClientAccesses) rateLimitations.get(clientIp);
                long keepAfter = config.rateLimit.getTimeUnit().toMillis(rateLimit.getValue());
                accesses.clearHistory(keepAfter);
                if (accesses.noAccess()) {
                    clientIpsToRemove.add(clientIp);
                }
            }
            clientIpsToRemove.forEach(clientIp -> {
                rateLimitations.remove(clientIp);
            });
        });
    }
}
