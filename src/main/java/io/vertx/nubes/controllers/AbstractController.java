package io.vertx.nubes.controllers;

import io.vertx.ext.apex.RoutingContext;

public abstract class AbstractController {

    /**
     * Do not call it render, render would be better for views (templates)
     * Renders a simple text to the client
     * TODO : remove if useless
     * 
     * @param context the RoutingContext
     * @param text the text to send to the client
     */
    protected void renderText(RoutingContext context, String text) {
        context.response().end(text);
    }
}
