package io.vertx.nubes.routing;

import io.vertx.core.http.HttpMethod;
import io.vertx.nubes.annotations.routing.DELETE;
import io.vertx.nubes.annotations.routing.OPTIONS;
import io.vertx.nubes.annotations.routing.POST;
import io.vertx.nubes.annotations.routing.PUT;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HttpMethodFactory {
    public static List<HttpMethod> fromAnnotatedMethod(Method method) {
        List<HttpMethod> methods = new ArrayList<HttpMethod>();
        methods.add(HttpMethod.GET); // by default
        if (method.isAnnotationPresent(POST.class)) {
            methods.add(HttpMethod.POST);
        }
        if (method.isAnnotationPresent(PUT.class)) {
            methods.add(HttpMethod.PUT);
        }
        if (method.isAnnotationPresent(DELETE.class)) {
            methods.add(HttpMethod.DELETE);
        }
        if (method.isAnnotationPresent(OPTIONS.class)) {
            methods.add(HttpMethod.OPTIONS);
        }
        return methods;
    }
}