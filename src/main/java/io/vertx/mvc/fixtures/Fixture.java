package io.vertx.mvc.fixtures;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public interface Fixture {
	public void startUp(Vertx vertx, Future<Void> future);
	public void tearDown(Vertx vertx, Future<Void> future);
}
