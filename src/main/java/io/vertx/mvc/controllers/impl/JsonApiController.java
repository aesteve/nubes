package io.vertx.mvc.controllers.impl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mvc.controllers.ApiController;

import java.util.List;
import java.util.Map;

public class JsonApiController extends ApiController {
	
	@Override
	protected String contentType() {
		return "application/json";
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected String marshallPayload(Object payload) {
		if (payload instanceof JsonObject) {
			return ((JsonObject)payload).toString();
		}
		if (payload instanceof JsonArray) {
			return ((JsonArray)payload).toString();
		}
		if (payload instanceof List) {
			return new JsonArray((List)payload).toString();
		}
		if (payload instanceof Map) {
			Map<String, Object> map = (Map<String, Object>)payload;
			JsonObject obj = new JsonObject(map);
			return obj.toString();
		}
		// TODO : register for custom types, or maybe just use Boon instead ?
		return null;
	}
}
