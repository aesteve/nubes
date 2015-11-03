package com.github.aesteve.vertx.nubes.services;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface Service {

	public void init(Vertx vertx, JsonObject config);

	public void start(Future<Void> future);

	public void stop(Future<Void> future);

}
