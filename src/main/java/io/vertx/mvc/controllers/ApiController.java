package io.vertx.mvc.controllers;



import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.filters.Finalizer;

/**
 * This is the basic API controller containing every method
 * you'll need to call in your own controllers.
 * 
 * It also contains a finalizer based on a 'payload' object so that you don't have to care about marshalling
 */
abstract public class ApiController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(ApiController.class);
	
	
    /**
     * Defines how you're going to marshall the API's payload object
     * 
     * @param payload the payload to marshal
     * @return marshalled payload as a String
     */
    public abstract void marshallPayload(Future<String> future, Object payload);
    
    /**
     * Defines how you're going to marshall the request body into a String
     * Default implementations are provided, but if you need some more advanced stuff : override it
     * 
     * @param desiredInstance the the desired instance
     * @return requestBody the request body as a String
     */
    public abstract <T> T fromRequestBody(Class<T> desiredInstance, String requestBody);
    

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

    protected<T> void resultAsPayload(RoutingContext context, AsyncResult<T> result) {
		if (result.succeeded()) {
			setPayload(context, result.result());
			context.next();
		} else {
			context.fail(result.cause());
		}
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
            Future<String> marshallFuture = Future.future();
            marshallFuture.setHandler(handler -> {
            	if (handler.succeeded()) {
            		response.end(handler.result());
                    response.setStatusCode(200);
            	} else {
            		context.fail(handler.cause());
            		log.error(handler.cause());
            	}
            });
            marshallPayload(marshallFuture, payload);
        } else {
            response.setStatusCode(204);
            response.end();
        }
    }
}
