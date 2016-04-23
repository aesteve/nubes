package com.github.aesteve.vertx.nubes.annotations.mixins;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Throttled {
}
