package io.vertx.mvc.controllers;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.BeforeFilter;
import io.vertx.mvc.annotations.Finalizer;

import java.util.List;

/**
 * This is the basic API controller containing every method
 * you'll need to call in your own controllers.
 * 
 * It also contains a finalizer based on a 'payload' object so that you don't have to care about marshalling
 */
abstract public class ApiController extends AbstractController {

    /**
     * Accepted content types for this controller
     * TODO : maybe use a "consume"-like annotation ?
     * 
     * @return the list of accepted content-types
     */
    abstract protected List<String> contentTypes();

    /**
     * Defines how you're going to marshall the API's payload object
     * 
     * @param payload the payload to marshal
     * @return marshalled payload as a String
     */
    abstract protected String marshallPayload(Object payload);

    /**
     * You'd better not redefine this since it's default implementation relies on the contentTypes() method above
     * But you're free to return one single static contentType if you want to
     * 
     * @param context
     * @return
     */
    protected String matchingContentType(RoutingContext context) {
        HttpServerRequest request = context.request();
        String accept = request.getHeader("Accept");
        if (accept == null) {
            return null;
        }
        // FIXME : parse header properly
        return contentTypes().stream().filter(contentType -> {
            return accept.toLowerCase().indexOf(contentType.toLowerCase()) > -1;
        }).findFirst().orElse(null);
    }

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
     * Before filter (called before your main handler)
     * 
     * Analyzes client's request to see if the client "Accepts" one of the contentType you're using
     * If not, returns a 406
     * 
     * @param context
     */
    @BeforeFilter
    public void setContentType(RoutingContext context) {
        HttpServerResponse response = context.response();
        String matchingContentType = matchingContentType(context);
        if (matchingContentType == null) {
            response.setStatusCode(406);
            response.end("Not acceptable");
            return;
        }
        response.headers().add("Content-Type", matchingContentType);
        context.next();
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
