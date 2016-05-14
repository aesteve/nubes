package com.github.aesteve.vertx.nubes.handlers;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractMethodInvocationHandler<T> implements Handler<RoutingContext> {

  protected final Method method;
  protected final Object instance;
  protected final boolean hasNext;
  protected final BiConsumer<RoutingContext, T> returnHandler;
  protected final boolean returnsSomething;
  private final Config config;
  private final Parameter[] parameters;
  protected boolean usesRoutingContext;
  protected boolean usesHttpResponse;

  protected AbstractMethodInvocationHandler(Object instance, Method method, Config config, boolean hasNext, BiConsumer<RoutingContext, T> returnHandler) {
    this.method = method;
    returnsSomething = !method.getReturnType().equals(Void.TYPE);
    this.hasNext = hasNext;
    parameters = method.getParameters();
    for (Parameter param : parameters) {
      Class<?> paramType = param.getType();
      if (paramType.equals(RoutingContext.class)) {
        usesRoutingContext = true;
      }
      if (paramType.equals(HttpServerResponse.class)) {
        usesHttpResponse = true;
      }
    }
    this.config = config;
    this.instance = instance;
    this.returnHandler = returnHandler;
  }

  @Override
  abstract public void handle(RoutingContext routingContext);

  protected Object[] getParameters(RoutingContext routingContext) throws WrongParameterException {
    List<Object> params = new ArrayList<>();
    for (Parameter param : parameters) {
      Object paramInstance = getParameterInstance(routingContext, param.getAnnotations(), param.getType(), param.getName());
      params.add(paramInstance);
    }
    return params.toArray();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass, String paramName) throws WrongParameterException {
    final TypedParamInjectorRegistry typeInjectors = config.getTypeInjectors();
    if (annotations.length == 0) { // rely on type
      final ParamInjector<?> injector = typeInjectors.getInjector(parameterClass);
      if (injector == null) {
        return null;
      }
      return injector.resolve(context);
    }
    if (annotations.length > 1) {
      throw new IllegalArgumentException("Every parameter should only have ONE annotation");
    }
    final Annotation annotation = annotations[0]; // rely on annotation
    final AnnotatedParamInjectorRegistry annotatedInjectors = config.getAnnotatedInjectors();
    final AnnotatedParamInjector injector = annotatedInjectors.getInjector(annotation.annotationType());
    if (injector == null) {
      return null;
    }
    return injector.resolve(context, annotation, paramName, parameterClass);
  }
}