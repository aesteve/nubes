package com.github.aesteve.vertx.nubes.annotations.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Verticle {

  boolean inheritsConfig() default false;

  int instances() default -1;

  boolean ha() default false;

  String isolationGroup() default "";

  boolean multiThreaded() default false;

  boolean worker() default false;
}