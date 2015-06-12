package com.github.aesteve.vertx.nubes.routing;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.handler.SessionHandler;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import com.github.aesteve.vertx.nubes.annotations.Blocking;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultMethodInvocationHandler;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.utils.Filter;

public class MVCRoute {

    private final String path;
    private final HttpMethod httpMethod;
    private final Object instance;
    private Set<Filter> beforeFilters;
    private Set<Filter> afterFilters;
    private Method mainHandler;
    private Set<Handler<RoutingContext>> handlers;
    private Set<Processor> processors;
    private TypedParamInjectorRegistry typedInjectors;
    private AnnotatedParamInjectorRegistry annotatedInjectors;
    private MVCRoute redirectRoute;
    private boolean usesAuth;
    private Vertx vertx;

    public MVCRoute(Object instance, String path, HttpMethod method, TypedParamInjectorRegistry typedInjectors, AnnotatedParamInjectorRegistry annotatedInjectors, boolean usesAuth, Vertx vertx) {
        this.instance = instance;
        this.path = path;
        this.httpMethod = method;
        this.beforeFilters = new TreeSet<Filter>();
        this.afterFilters = new TreeSet<Filter>();
        this.handlers = new LinkedHashSet<Handler<RoutingContext>>();
        this.processors = new LinkedHashSet<Processor>();
        this.typedInjectors = typedInjectors;
        this.annotatedInjectors = annotatedInjectors;
        this.usesAuth = usesAuth;
        this.vertx = vertx;
    }

    public void redirectTo(MVCRoute anotherRoute) {
        redirectRoute = anotherRoute;
    }

    public void addProcessor(Processor processor) {
        processors.add(processor);
    }

    public void addProcessors(Set<Processor> processors) {
        this.processors.addAll(processors);
    }

    public void addProcessorsFirst(Set<Processor> processors) {
        Set<Processor> oldProcessors = new LinkedHashSet<Processor>(this.processors);
        this.processors = new LinkedHashSet<Processor>(oldProcessors.size() + processors.size());
        this.processors.addAll(processors);
        this.processors.addAll(oldProcessors);
    }

    public void attachHandler(Handler<RoutingContext> handler) {
        handlers.add(handler);
    }

    public void attachHandlers(Set<Handler<RoutingContext>> newHandlers) {
        handlers.addAll(newHandlers);
    }

    public void setMainHandler(Method mainHandler) {
        this.mainHandler = mainHandler;
    }

    public void addBeforeFilters(Set<Filter> beforeFilters) {
        this.beforeFilters.addAll(beforeFilters);
    }

    public void addAfterFilters(Set<Filter> afterFilters) {
        this.afterFilters.addAll(afterFilters);
    }

    public String path() {
        return path;
    }

    public HttpMethod method() {
        return httpMethod;
    }

    public void attachHandlersToRouter(Router router, HttpMethod httpMethod, String path) {
        if (httpMethod == null) {
            httpMethod = this.httpMethod;
        }
        if (path == null) {
            path = this.path;
        }
        final HttpMethod httpMethodFinal = httpMethod;
        final String pathFinal = path;
        if (usesAuth) {
            router.route(httpMethodFinal, pathFinal).handler(CookieHandler.create());
            router.route(httpMethodFinal, pathFinal).handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        }
        processors.forEach(processor -> {
            router.route(httpMethodFinal, pathFinal).handler(processor::preHandle);
        });
        handlers.forEach(handler -> {
            router.route(httpMethodFinal, pathFinal).handler(handler);
        });
        beforeFilters.forEach(filter -> {
            setHandler(router, filter.method(), httpMethodFinal, pathFinal);
        });
        setHandler(router, mainHandler, httpMethodFinal, pathFinal);
        if (redirectRoute != null) {
            // intercepted -> redirected => do not call post processing handlers
            redirectRoute.attachHandlersToRouter(router, httpMethod, path);
        }
        afterFilters.forEach(filter -> {
            setHandler(router, filter.method(), httpMethodFinal, pathFinal);
        });
        processors.forEach(processor -> {
            router.route(httpMethodFinal, pathFinal).handler(processor::postHandle);
        });

    }

    private void setHandler(Router router, Method method, HttpMethod httpMethod, String path) {
        Handler<RoutingContext> handler = new DefaultMethodInvocationHandler(instance, method, typedInjectors, annotatedInjectors);
        if (method.isAnnotationPresent(Blocking.class)) {
            router.route(httpMethod, path).blockingHandler(handler);
        } else {
            router.route(httpMethod, path).handler(handler);
        }
    }

    @Override
    public String toString() {
        return "Route : " + httpMethod.toString() + " " + path();
    }
}
