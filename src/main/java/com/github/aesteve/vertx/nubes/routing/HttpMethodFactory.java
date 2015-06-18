package com.github.aesteve.vertx.nubes.routing;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.annotations.routing.http.CONNECT;
import com.github.aesteve.vertx.nubes.annotations.routing.http.DELETE;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.HEAD;
import com.github.aesteve.vertx.nubes.annotations.routing.http.OPTIONS;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PATCH;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PUT;
import com.github.aesteve.vertx.nubes.annotations.routing.http.TRACE;

public class HttpMethodFactory {

    public static Map<HttpMethod, String> fromAnnotatedMethod(Method method) {
        Map<HttpMethod, String> methods = new HashMap<HttpMethod, String>();
        for (Annotation annot : method.getDeclaredAnnotations()) {
            Class<? extends Annotation> annotClass = annot.annotationType();
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
        return methods;
    }

    public static boolean isRouteMethod(Method method) {
        return method.isAnnotationPresent(CONNECT.class)
                        || method.isAnnotationPresent(DELETE.class)
                        || method.isAnnotationPresent(GET.class)
                        || method.isAnnotationPresent(HEAD.class)
                        || method.isAnnotationPresent(OPTIONS.class)
                        || method.isAnnotationPresent(PATCH.class)
                        || method.isAnnotationPresent(POST.class)
                        || method.isAnnotationPresent(PUT.class)
                        || method.isAnnotationPresent(TRACE.class);
    }
}
