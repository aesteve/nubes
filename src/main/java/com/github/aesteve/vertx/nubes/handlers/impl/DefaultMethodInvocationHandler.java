package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.handlers.AbstractMethodInvocationHandler;

public class DefaultMethodInvocationHandler extends AbstractMethodInvocationHandler {

    public DefaultMethodInvocationHandler(Object instance, Method method, Config config) {
        super(instance, method, config);
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