package com.github.aesteve.vertx.nubes.annotations.cookies;

import java.lang.annotation.*;

/**
 * Marker interface to specify that this Controller or specific method uses cookies
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Cookies {

}