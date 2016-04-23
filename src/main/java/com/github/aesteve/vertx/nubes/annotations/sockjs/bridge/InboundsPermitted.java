package com.github.aesteve.vertx.nubes.annotations.sockjs.bridge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InboundsPermitted {

  InboundPermitted[] value();

}
