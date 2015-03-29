package io.vertx.mvc.controllers.impl;

import io.vertx.mvc.controllers.ApiController;

import org.boon.json.JsonFactory;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;

/**
 * Default implementation of an ApiController for "application/json" content-type
 * It uses the "boon" library under the hood, but you can provide your own json marshaller
 * Or even your own boon serializer if the default one doesn't fit your needs
 */
public class JsonApiController extends ApiController {

    private JsonSerializer serializer;
    private ObjectMapper mapper;

    public JsonApiController() {
        serializer = createSerializer();
        mapper = createMapper();
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
     * Override this method if you want to use a custom boon's object mapper
     * 
     * @return a boon ObjectMapper that will be used to read from the request body
     */
    protected ObjectMapper createMapper() {
    	return JsonFactory.create();
    }

    /**
     * By default, uses the boon serializer specified in createSerializer (or the default one)
     * You can override this method to provide your own way to marshall json
     * 
     * @param the payload object
     * @return the payload in a json String
     */
    @Override
	public String marshallPayload(Object payload) {
        return serializer.serialize(payload).toString();
    }
    
    /**
     * Read from request body
     * @param <T>
     */
    @Override
	public <T> T fromRequestBody(Class<T> desiredInstance, String requestBody) {
    	return mapper.fromJson(requestBody, desiredInstance);
    }
}
