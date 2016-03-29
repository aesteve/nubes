package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.context.ClientAccesses;
import com.github.aesteve.vertx.nubes.context.RateLimit;

public class RateLimitationHandler implements Handler<RoutingContext> {

	private final RateLimit rateLimit;

	public static RateLimitationHandler create(Config config) {
		return new RateLimitationHandler(config.rateLimit);
	}

	private RateLimitationHandler(RateLimit rateLimit) {
		this.rateLimit = rateLimit;
	}

	@Override
	public void handle(RoutingContext context) {
		Vertx vertx = context.vertx();
		LocalMap<Object, Object> rateLimitations = vertx.sharedData().getLocalMap("mvc.rateLimitation");
		String clientIp = context.request().remoteAddress().host();
		JsonObject json = (JsonObject) rateLimitations.get(clientIp);
		ClientAccesses accesses;
		if (json == null) {
			accesses = new ClientAccesses();
		} else {
			accesses = ClientAccesses.fromJsonObject(json);
		}
		accesses.newAccess();
		rateLimitations.put(clientIp, accesses.toJsonObject());
		if (accesses.isOverLimit(rateLimit)) {
			context.fail(420);
		} else {
			context.next();
		}
	}

}
