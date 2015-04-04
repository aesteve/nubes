package io.vertx.mvc.fixtures;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public abstract class Fixture implements Comparable<Fixture> {
	public abstract int executionOrder();
	public abstract void startUp(Vertx vertx, Future<Void> future);
	public abstract void tearDown(Vertx vertx, Future<Void> future);
	
	@Override
	public int compareTo(Fixture other) {
		return Integer.compare(this.executionOrder(), other.executionOrder());
	}
}
