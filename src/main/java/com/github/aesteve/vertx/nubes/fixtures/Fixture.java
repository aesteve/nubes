package com.github.aesteve.vertx.nubes.fixtures;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public interface Fixture extends Comparable<Fixture> {

  int executionOrder();

  void startUp(Vertx vertx, Future<Void> future);

  void tearDown(Vertx vertx, Future<Void> future);

  @Override
  default int compareTo(Fixture other) {
    return Integer.compare(this.executionOrder(), other.executionOrder());
  }
}
