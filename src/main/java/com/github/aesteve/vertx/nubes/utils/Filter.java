package com.github.aesteve.vertx.nubes.utils;

import java.lang.reflect.Method;

import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;

public class Filter implements Comparable<Filter> {

    private int order;
    private Method method;

    public Filter(Method method, AfterFilter annot) {
        this.method = method;
        this.order = annot.value();
    }

    public Filter(Method method, BeforeFilter annot) {
        this.method = method;
        this.order = annot.value();
    }

    @Override
    public int compareTo(Filter other) {
        if (other == null) {
            return -1;
        }
        return Integer.compare(order, other.order);
    }

    public Method method() {
        return method;
    }

}
