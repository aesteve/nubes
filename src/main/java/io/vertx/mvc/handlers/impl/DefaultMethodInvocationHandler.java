package io.vertx.mvc.handlers.impl;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.exceptions.HttpException;
import io.vertx.mvc.handlers.AbstractMethodInvocationHandler;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import io.vertx.mvc.reflections.injectors.typed.TypedParamInjectorRegistry;

import java.lang.reflect.Method;

public class DefaultMethodInvocationHandler extends AbstractMethodInvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultMethodInvocationHandler.class);

    public DefaultMethodInvocationHandler(Object instance, Method method, TypedParamInjectorRegistry typedInjectors, AnnotatedParamInjectorRegistry annotInjectors) {
        super(instance, method, typedInjectors, annotInjectors);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        if (routingContext.response().ended()) {
            return;
        }
        try {
            method.invoke(instance, getParameters(routingContext));
        } catch (HttpException he) {
            routingContext.response().setStatusCode(he.getStatusCode());
            routingContext.response().setStatusMessage(he.getStatusMessage());
            routingContext.fail(he.getStatusCode());
        } catch (Throwable others) {
            log.error(others);
            routingContext.fail(others);
        }

    }
}