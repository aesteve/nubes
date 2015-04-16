package io.vertx.mvc.handlers.impl;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.exceptions.BadRequestException;
import io.vertx.mvc.handlers.AbstractMethodInvocationHandler;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import io.vertx.mvc.reflections.injectors.typed.TypedParamInjectorRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultMethodInvocationHandler extends AbstractMethodInvocationHandler {

    public DefaultMethodInvocationHandler(Object instance, Method method, TypedParamInjectorRegistry typedInjectors, AnnotatedParamInjectorRegistry annotInjectors) {
        super(instance, method, typedInjectors, annotInjectors);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        System.out.println("invoking ?" + method.getName());
        System.out.println("context failed ?" + routingContext.failed());

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