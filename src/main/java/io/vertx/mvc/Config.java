package io.vertx.mvc;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mvc.context.RateLimit;
import io.vertx.mvc.exceptions.MissingConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Config {
	
	public List<String> controllerPackages;
	public List<String> fixturePackages;
	public RateLimit rateLimit;
	public String webroot;
	public String assetsPath;
	public String tplDir;
	public boolean displayErrors;
	
	/**
	 * TODO : check config instead of throwing exceptions
	 * @param json
	 * @return config
	 */
	@SuppressWarnings("unchecked")
	public static Config fromJsonObject(JsonObject json) throws MissingConfigurationException {
		Config config = new Config();
		JsonArray controllers = json.getJsonArray("controller-packages");
		if (controllers == null) {
			throw new MissingConfigurationException("controller-packages");
		}
		config.controllerPackages = controllers.getList();
		JsonArray fixtures = json.getJsonArray("fixture-packages");
		if (fixtures != null) {
			config.fixturePackages = fixtures.getList();
		} else {
			config.fixturePackages = new ArrayList<String>();
		}
		JsonObject rateLimitJson = json.getJsonObject("throttling");
		if (rateLimitJson != null) {
			int count = rateLimitJson.getInteger("count");
			int value = rateLimitJson.getInteger("time-frame");
			TimeUnit timeUnit = TimeUnit.valueOf(rateLimitJson.getString("time-unit"));
			config.rateLimit = new RateLimit(count, value, timeUnit);
		}
		config.webroot = json.getString("webroot", "web/assets");
		config.assetsPath = json.getString("static-path", "/assets");
		config.tplDir = json.getString("views-dir", "web/views");
		config.displayErrors = json.getBoolean("display-errors", Boolean.FALSE);
		return config;
	}
}
