package io.vertx.mvc.controllers.impl;

import io.vertx.mvc.controllers.ApiController;

import java.util.ArrayList;
import java.util.List;

import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;

public class JsonApiController extends ApiController {
	
	private JsonSerializer serializer;
	
	// let users override it if they need to
	protected JsonSerializer createSerializer(){
		return new JsonSerializerFactory().create();
	}
	
	public JsonApiController() {
		serializer = createSerializer();
	}
	
	@Override
	protected List<String> contentTypes() {
		List<String> contentTypes = new ArrayList<String>();
		contentTypes.add("application/json");
		return contentTypes;
	}
	
	@Override
	protected String marshallPayload(Object payload) {
		return serializer.serialize(payload).toString();
	}
}
