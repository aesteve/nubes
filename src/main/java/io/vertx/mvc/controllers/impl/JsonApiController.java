package io.vertx.mvc.controllers.impl;

import io.vertx.mvc.controllers.ApiController;

import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;

/**
 * Default implementation of an ApiController for "application/json" content-type
 * It uses the "boon" library under the hood, but you can provide your own json marshaller
 * Or even your own boon serializer if the default one doesn't fit your needs
 */
public class JsonApiController extends ApiController {

    private JsonSerializer serializer;

    public JsonApiController() {
        serializer = createSerializer();
    }

    /**
     * Override this method if you want to use a custom boon's serializer
     * 
     * @return a boon JsonSerializer that will be used to marshall your payload as json
     */
    protected JsonSerializer createSerializer() {
        return new JsonSerializerFactory().create();
    }

    /**
     * By default, uses the boon serializer specified in createSerializer (or the default one)
     * You can override this method to provide your own json serializer
     * 
     * @param the payload object
     * @return the payload in a json String
     */
    @Override
    protected String marshallPayload(Object payload) {
        return serializer.serialize(payload).toString();
    }
}
