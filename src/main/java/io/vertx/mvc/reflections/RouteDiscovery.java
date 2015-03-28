package io.vertx.mvc.reflections;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.handler.CookieHandler;
import io.vertx.mvc.Config;
import io.vertx.mvc.annotations.AfterFilter;
import io.vertx.mvc.annotations.BeforeFilter;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Finalizer;
import io.vertx.mvc.annotations.Paginated;
import io.vertx.mvc.annotations.Path;
import io.vertx.mvc.annotations.Throttled;
import io.vertx.mvc.annotations.UsesCookies;
import io.vertx.mvc.annotations.params.RequestBody;
import io.vertx.mvc.routing.HttpMethodFactory;
import io.vertx.mvc.routing.MVCRoute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

public class RouteDiscovery {
	
	private Router router;
	private Config config;
	
	
	public RouteDiscovery(Router router, Config config) {
		this.router = router;
		this.config = config;
	}
	
	public void createRoutes() {
        List<MVCRoute> routes = extractRoutesFromControllers();
        CookieHandler cookieHandler = CookieHandler.create();
        routes.forEach(route -> {
            if (route.usesCookies()) {
                route.attachHandlersToRouter(router, cookieHandler);
            } else {
                route.attachHandlersToRouter(router);
            }

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
            	
            	Class<?> bodyClass = null;
                Class<?>[] parameterClasses = method.getParameterTypes();
                Annotation[][] parametersAnnotations = method.getParameterAnnotations();
                for (int i = 0; i< parameterClasses.length; i++) {
                	Annotation[] paramAnnotations = parametersAnnotations[i];
                	if (paramAnnotations != null && paramAnnotations.length > 0) {
                		Annotation annotation = paramAnnotations[0];
                		if (annotation instanceof RequestBody) {
                			bodyClass = parameterClasses[i];
                		}
                	} 
                }
            	
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
                    if (bodyClass != null) {
                    	route.setBodyClass(bodyClass);
                    }
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
}
