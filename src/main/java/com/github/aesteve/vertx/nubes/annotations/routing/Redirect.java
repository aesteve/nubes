package com.github.aesteve.vertx.nubes.annotations.routing;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface Redirect {

  int code() default 302;

  String value();
}
