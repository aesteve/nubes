package com.github.aesteve.vertx.nubes.routing;

import com.github.aesteve.vertx.nubes.annotations.routing.http.*;
import io.vertx.core.http.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class HttpMethodFactory {

  private static List<Class<? extends Annotation>> annotClasses = Arrays.asList(
      CONNECT.class,
      DELETE.class,
      GET.class,
      HEAD.class,
      OPTIONS.class,
      PATCH.class,
      POST.class,
      PUT.class,
      TRACE.class
  );

  private HttpMethodFactory() {}

  public static Map<HttpMethod, String> fromAnnotatedMethod(Method method) {
    Map<HttpMethod, String> methods = new EnumMap<>(HttpMethod.class);
    for (Annotation annot : method.getDeclaredAnnotations()) {
      Class<? extends Annotation> annotClass = annot.annotationType();
      putIfHttpMethod(methods, annot, annotClass);
    }
    return methods;
  }

  public static boolean isRouteMethod(Method method) {
    return annotClasses.stream().anyMatch(method::isAnnotationPresent);
  }

  private static void putIfHttpMethod(Map<HttpMethod, String> methods, Annotation annot, Class<? extends Annotation> annotClass) {
    if (annotClass.equals(CONNECT.class)) {
      CONNECT connect = (CONNECT) annot;
      methods.put(HttpMethod.CONNECT, connect.value());
    }
    if (annotClass.equals(DELETE.class)) {
      DELETE delete = (DELETE) annot;
      methods.put(HttpMethod.DELETE, delete.value());
    }
    if (annotClass.equals(GET.class)) {
      GET get = (GET) annot;
      methods.put(HttpMethod.GET, get.value());
    }
    if (annotClass.equals(HEAD.class)) {
      HEAD head = (HEAD) annot;
      methods.put(HttpMethod.HEAD, head.value());
    }
    if (annotClass.equals(OPTIONS.class)) {
      OPTIONS options = (OPTIONS) annot;
      methods.put(HttpMethod.OPTIONS, options.value());
    }
    if (annotClass.equals(PATCH.class)) {
      PATCH patch = (PATCH) annot;
      methods.put(HttpMethod.PATCH, patch.value());
    }
    if (annotClass.equals(POST.class)) {
      POST post = (POST) annot;
      methods.put(HttpMethod.POST, post.value());
    }
    if (annotClass.equals(PUT.class)) {
      PUT put = (PUT) annot;
      methods.put(HttpMethod.PUT, put.value());
    }
    if (annotClass.equals(TRACE.class)) {
      TRACE trace = (TRACE) annot;
      methods.put(HttpMethod.TRACE, trace.value());
    }
  }

}
