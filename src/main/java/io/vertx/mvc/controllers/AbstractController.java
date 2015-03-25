package io.vertx.mvc.controllers;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.context.PaginationContext;

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

    /**
     * Retrieves the PaginationContext if you've annotated your method as @Paginated
     * 
     * @param context the RoutingContext
     * @return the current PaginationContext
     * @throws IllegalStateException if you didn't annotated your method as @Paginated
     */
    protected PaginationContext getPaginationContext(RoutingContext context) {
        PaginationContext pageContext = (PaginationContext) context.data().get(PaginationContext.DATA_ATTR);
        if (pageContext == null) {
            throw new IllegalStateException("In order to retrieve pagination context you have to annotate your method with @Paginated");
        }
        return pageContext;
    }
}
