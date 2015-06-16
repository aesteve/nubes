package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.aesteve.vertx.nubes.handlers.AbstractMethodInvocationHandler;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;

public class DefaultMethodInvocationHandler extends AbstractMethodInvocationHandler {

    public DefaultMethodInvocationHandler(Object instance, Method method, TypedParamInjectorRegistry typedInjectors, AnnotatedParamInjectorRegistry annotInjectors) {
        super(instance, method, typedInjectors, annotInjectors);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        if (routingContext.response().ended()) {
            return;
        }
        if (routingContext.failed()) {
            return;
        }
        Object[] parameters = null;
        try {
            parameters = getParameters(routingContext);
        } catch (Exception e) {
            routingContext.fail(400);
            return;
        }
        try {
            method.invoke(instance, parameters);
        } catch (InvocationTargetException ite) {
            routingContext.fail(ite.getCause());
            return;
        } catch (Throwable others) {
            routingContext.fail(others);
            return;
        }
    }
}