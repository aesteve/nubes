package com.github.aesteve.vertx.nubes.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

public abstract class AbstractMethodInvocationHandler implements Handler<RoutingContext> {

    protected Method method;
    protected Object instance;
    protected Config config;

    public AbstractMethodInvocationHandler(Object instance, Method method, Config config) {
        this.method = method;
        this.config = config;
        this.instance = instance;
    }

    @Override
    abstract public void handle(RoutingContext routingContext);

    protected Object[] getParameters(RoutingContext routingContext) {
        List<Object> parameters = new ArrayList<Object>();
        Class<?>[] parameterClasses = method.getParameterTypes();
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterClasses.length; i++) {
            Object paramInstance = getParameterInstance(routingContext, parametersAnnotations[i], parameterClasses[i]);
            parameters.add(paramInstance);
        }
        return parameters.toArray();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass) {
        if (annotations.length == 0) { // rely on type
            ParamInjector<?> injector = config.typeInjectors.getInjector(parameterClass);
            if (injector == null) {
                return null;
            }
            try {
                return injector.resolve(context);
            } catch (Exception e) {
                DefaultErrorHandler.badRequest(context, "Invalid parameter for : " + parameterClass);
            }
        }
        if (annotations.length > 1) {
            throw new IllegalArgumentException("Every parameter should only have ONE annotation");
        }
        Annotation annotation = annotations[0]; // rely on annotation
        AnnotatedParamInjector injector = (AnnotatedParamInjector) config.annotInjectors.getInjector(annotation.annotationType());
        if (injector != null) {
            try {
                return injector.resolve(context, annotation, parameterClass);
            } catch (Exception e) {
                DefaultErrorHandler.badRequest(context, "Invalid parameter value for : " + parameterClass);
            }
        }
        return null;
    }
}