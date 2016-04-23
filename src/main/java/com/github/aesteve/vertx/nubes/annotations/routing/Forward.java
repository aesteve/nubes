package com.github.aesteve.vertx.nubes.annotations.routing;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface Forward {
  Class<?> controller();

  String action();
}
