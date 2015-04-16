package io.vertx.mvc.reflections;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.Config;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.filters.AfterFilter;
import io.vertx.mvc.annotations.filters.BeforeFilter;
import io.vertx.mvc.annotations.routing.Path;
import io.vertx.mvc.annotations.routing.ServerRedirect;
import io.vertx.mvc.handlers.AnnotationProcessor;
import io.vertx.mvc.handlers.AnnotationProcessorRegistry;
import io.vertx.mvc.handlers.Processor;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import io.vertx.mvc.reflections.injectors.typed.TypedParamInjectorRegistry;
import io.vertx.mvc.routing.HttpMethodFactory;
import io.vertx.mvc.routing.MVCRoute;
import io.vertx.mvc.services.Service;
import io.vertx.mvc.services.ServiceRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class RouteDiscovery {

    private Router router;
    private Config config;
    private AnnotationProcessorRegistry apRegistry;
    private Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers;
    private Map<Class<?>, Processor> typeProcessors;
    private TypedParamInjectorRegistry typedInjectors;
    private AnnotatedParamInjectorRegistry annotInjectors;
    private ServiceRegistry serviceRegistry;
    private RouteRegistry routeRegistry;

    public RouteDiscovery(Router router, Config config, Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers, Map<Class<?>, Processor> typeProcessors, AnnotationProcessorRegistry apRegistry, TypedParamInjectorRegistry typedInjectors, AnnotatedParamInjectorRegistry annotInjectors, ServiceRegistry serviceRegistry) {
        this.router = router;
        this.config = config;
        this.annotationHandlers = annotationHandlers;
        this.typeProcessors = typeProcessors;
        this.apRegistry = apRegistry;
        this.typedInjectors = typedInjectors;
        this.annotInjectors = annotInjectors;
        this.serviceRegistry = serviceRegistry;
        this.routeRegistry = new RouteRegistry();
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
        if (base != null) {
            basePath = base.value();
        }
        for (Method method : controller.getDeclaredMethods()) {
            if (method.getAnnotation(Path.class) != null) {
                Set<Handler<RoutingContext>> paramsHandlers = new LinkedHashSet<Handler<RoutingContext>>();
                Class<?>[] parameterClasses = method.getParameterTypes();
                Annotation[][] parametersAnnotations = method.getParameterAnnotations();
                for (int i = 0; i < parameterClasses.length; i++) {
                    Class<?> parameterClass = parameterClasses[i];
                    Processor typeProcessor = typeProcessors.get(parameterClass);
                    if (typeProcessor != null) {
                        processors.add(typeProcessor);
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
                    MVCRoute route = new MVCRoute(instance, basePath + path.value(), httpMethod, typedInjectors, annotInjectors);
                    for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
                        Set<Handler<RoutingContext>> handler = annotationHandlers.get(methodAnnotation.annotationType());
                        if (handler != null) {
                            route.attachHandlers(handler);
                        }
                        AnnotationProcessor<?> annProcessor = apRegistry.getProcessor(methodAnnotation);
                        if (annProcessor != null) {
                        	System.out.println("add annotation processor : " + annProcessor + " onto : " + route);
                            route.addProcessor(annProcessor);
                        }
                    }
                    if (basePath.contains("redirect")) {
                    	System.out.println("processors = "+processors);
                    }
                    route.addProcessors(processors);
                    route.attachHandlers(paramsHandlers);
                    route.setMainHandler(method);
                    routes.add(route);
//                    routeRegistry.register(controller, method, route);
//                    if (method.isAnnotationPresent(ServerRedirect.class)) {
//                    	ServerRedirect redirect = method.getAnnotation(ServerRedirect.class);
//                    	routeRegistry.bindRedirect(route, redirect);
//                    }
                }
            }
        }
        extractFiltersFromController(routes, controller);
        return routes;
    }

    private void extractFiltersFromController(List<MVCRoute> routes, Class<?> controller) {
        List<Method> beforeFilters = new ArrayList<Method>();
        List<Method> afterFilters = new ArrayList<Method>();
        for (Method method : controller.getDeclaredMethods()) {
            if (method.getAnnotation(BeforeFilter.class) != null) {
                beforeFilters.add(method);
            } else if (method.getAnnotation(AfterFilter.class) != null) {
                afterFilters.add(method);
            }
        }
        Set<Processor> processors = new LinkedHashSet<Processor>();
        for (Annotation annotation : controller.getDeclaredAnnotations()) {
            AnnotationProcessor<?> controllerProcessor = apRegistry.getProcessor(annotation);
            if (controllerProcessor != null) {
                processors.add(controllerProcessor);
            }
        }
        for (MVCRoute route : routes) {
            route.addProcessorsFirst(processors);
            route.addBeforeFilters(beforeFilters);
            route.addAfterFilters(afterFilters);
        }
        if (controller.getSuperclass() != null && !controller.getSuperclass().equals(Object.class)) {
            extractFiltersFromController(routes, controller.getSuperclass());
        }
    }

    private void injectServicesIntoController(Object instance) throws IllegalAccessException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            Service service = serviceRegistry.get(field);
            if (service != null) {
                field.setAccessible(true);
                field.set(instance, serviceRegistry.get(field));
            }
        }
    }
}
