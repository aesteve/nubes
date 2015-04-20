package io.vertx.nubes.handlers.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.nubes.exceptions.BadRequestException;
import io.vertx.nubes.handlers.AbstractMethodInvocationHandler;
import io.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import io.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        } catch (BadRequestException bre) {
            routingContext.fail(bre);
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