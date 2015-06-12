package com.github.aesteve.vertx.nubes.routing;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private static Map<Class<? extends Annotation>, HttpMethod> conversionTable = new HashMap<Class<? extends Annotation>, HttpMethod>();
    static {
        conversionTable.put(GET.class, HttpMethod.GET);
        conversionTable.put(POST.class, HttpMethod.POST);
        conversionTable.put(PUT.class, HttpMethod.PUT);
        conversionTable.put(PATCH.class, HttpMethod.PATCH);
        conversionTable.put(DELETE.class, HttpMethod.DELETE);
        conversionTable.put(OPTIONS.class, HttpMethod.OPTIONS);
        conversionTable.put(TRACE.class, HttpMethod.TRACE);
        conversionTable.put(HEAD.class, HttpMethod.HEAD);
        conversionTable.put(CONNECT.class, HttpMethod.CONNECT);
    }

    public static List<HttpMethod> fromAnnotatedMethod(Method method) {
        List<HttpMethod> methods = new ArrayList<HttpMethod>();
        for (Annotation annot : method.getDeclaredAnnotations()) {
            HttpMethod http = conversionTable.get(annot.annotationType());
            if (http != null) {
                methods.add(http);
            }
        }
        if (methods.isEmpty()) {
            methods.add(HttpMethod.GET); // by default
        }
        return methods;
    }
}
