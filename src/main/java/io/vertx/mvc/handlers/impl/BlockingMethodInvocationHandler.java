package io.vertx.mvc.handlers.impl;

import java.lang.reflect.Method;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.exceptions.HttpException;
import io.vertx.mvc.handlers.AbstractMethodInvocationHandler;
import io.vertx.mvc.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import io.vertx.mvc.reflections.injectors.typed.TypedParamInjectorRegistry;

public class BlockingMethodInvocationHandler extends AbstractMethodInvocationHandler {

    public BlockingMethodInvocationHandler(Object instance, Method method, TypedParamInjectorRegistry typedInjectors, AnnotatedParamInjectorRegistry annotInjectors) {
        super(instance, method, typedInjectors, annotInjectors);
    }

    @Override
    public void handle(RoutingContext context) {
        if (context.response().ended()) {
            return;
        }
        context.vertx().executeBlocking(future -> {
            try {
                Object result = method.invoke(instance, getParameters(context));
                future.complete(result);
            } catch (Exception e) {
                future.fail(e);
            }
        }, result -> {
            if (result.succeeded()) {
                if (result.result() != null) {
                    context.put("return-" + method.getName(), result.result());
                }
                context.next();
            } else {
                Throwable cause = result.cause();
                if (cause instanceof HttpException) {
                    HttpException he = (HttpException) cause;
                    context.response().setStatusCode(he.getStatusCode());
                    context.response().setStatusMessage(he.getStatusMessage());
                    context.fail(he.getStatusCode());
                } else {
                    context.fail(cause);
                }
            }
        });

    }

}
