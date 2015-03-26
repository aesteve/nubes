package io.vertx.mvc.controllers;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Finalizer;

/**
 * This is the basic API controller containing every method
 * you'll need to call in your own controllers.
 * 
 * It also contains a finalizer based on a 'payload' object so that you don't have to care about marshalling
 */
abstract public class ApiController extends AbstractController {

    /**
     * Defines how you're going to marshall the API's payload object
     * 
     * @param payload the payload to marshal
     * @return marshalled payload as a String
     */
    abstract protected String marshallPayload(Object payload);

    /**
     * This is the core of the API
     * The payload is the object you expect the client to receive as request body
     * This method returns it as its "Object" form, it's still unmarshalled so that you can do whatever you want with it
     * 
     * @param context
     * @return the response payload
     */
    protected Object getPayload(RoutingContext context) {
        return context.data().get("payload");
    }

    /**
     * Call this method in your routing method to set the response payload
     * After everything has been done, the response will be sent to the client by marshalling this object
     * If you set null, or don't call it, an http code 204 will be returned
     * 
     * @param context
     * @param payload
     */
    protected void setPayload(RoutingContext context, Object payload) {
        context.data().put("payload", payload);
    }

    /**
     * The response finalizer (will be called last, after every of your handlers)
     * 
     * Marshalls the payload object into the http response and sends it to the client
     * 
     * @param context
     */
    @Finalizer
    public void sendResponse(RoutingContext context) {
        HttpServerResponse response = context.response();
        Object payload = getPayload(context);
        if (payload != null) {
            response.setStatusCode(200);
            response.end(marshallPayload(payload));
        } else {
            response.setStatusCode(204);
            response.end();
        }
    }
}
