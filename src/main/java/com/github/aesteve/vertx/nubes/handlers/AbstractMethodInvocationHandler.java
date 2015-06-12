package com.github.aesteve.vertx.nubes.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;

public abstract class AbstractMethodInvocationHandler implements Handler<RoutingContext> {

    protected Method method;
    protected Object instance;
    protected TypedParamInjectorRegistry typedInjectors;
    protected AnnotatedParamInjectorRegistry annotInjectors;

    public AbstractMethodInvocationHandler(Object instance, Method method, TypedParamInjectorRegistry typedInjectors, AnnotatedParamInjectorRegistry annotInjectors) {
        this.method = method;
        this.instance = instance;
        this.typedInjectors = typedInjectors;
        this.annotInjectors = annotInjectors;
    }

    @Override
    abstract public void handle(RoutingContext routingContext);

    protected Object[] getParameters(RoutingContext routingContext) throws BadRequestException {
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
    protected Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass) throws BadRequestException {
        if (annotations.length == 0) { // rely on type
            ParamInjector<?> injector = typedInjectors.getInjector(parameterClass);
            if (injector == null) {
                return null;
            }
            return injector.resolve(context);
        }
        if (annotations.length > 1) {
            throw new IllegalArgumentException("Every parameter should only have ONE annotation");
        }
        Annotation annotation = annotations[0]; // rely on annotation
        AnnotatedParamInjector injector = (AnnotatedParamInjector) annotInjectors.getInjector(annotation.annotationType());
        if (injector != null) {
            return injector.resolve(context, annotation, parameterClass);
        }
        return null;
    }
}