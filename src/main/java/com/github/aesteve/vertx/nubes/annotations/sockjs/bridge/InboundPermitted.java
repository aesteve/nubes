package com.github.aesteve.vertx.nubes.annotations.sockjs.bridge;

import java.lang.annotation.*;

@Repeatable(InboundsPermitted.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InboundPermitted {

  String address() default "";

  String addressRegex() default "";

  String requiredAuthority() default "";

}
