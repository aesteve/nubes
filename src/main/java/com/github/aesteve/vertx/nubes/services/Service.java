package com.github.aesteve.vertx.nubes.services;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface Service {

  void init(Vertx vertx, JsonObject config);

  void start(Future<Void> future);

  void stop(Future<Void> future);

}
