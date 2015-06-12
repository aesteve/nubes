package com.github.aesteve.vertx.nubes.context;

import java.util.concurrent.TimeUnit;

public class RateLimit {

    private final int count;
    private final int value;
    private final TimeUnit timeUnit;

    public RateLimit(int count, int value, TimeUnit timeUnit) {
        this.count = count;
        this.value = value;
        this.timeUnit = timeUnit;
    }

    public int getCount() {
        return count;
    }

    public int getValue() {
        return value;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public String toString() {
        return "Rate limit : {count=" + count + ", value=" + value + ", timeUnit=" + timeUnit + "}";
    }

}
