package io.vertx.mvc;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mvc.context.RateLimit;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Config {
	
	public List<String> controllerPackages;
	public RateLimit rateLimit;
	
	/**
	 * TODO : check config instead of throwing exceptions
	 * @param json
	 * @return config
	 */
	@SuppressWarnings("unchecked")
	public static Config fromJsonObject(JsonObject json) {
		System.out.println("Read config : "+json);
		Config config = new Config();
		JsonArray controllers = json.getJsonArray("controller-packages");
		config.controllerPackages = controllers.getList();
		JsonObject rateLimitJson = json.getJsonObject("throttling");
		int count = rateLimitJson.getInteger("count");
		int value = rateLimitJson.getInteger("time-frame");
		TimeUnit timeUnit = TimeUnit.valueOf(rateLimitJson.getString("time-unit"));
		config.rateLimit = new RateLimit(count, value, timeUnit);
		return config;
	}
}
