package com.github.aesteve.vertx.nubes.utils.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class SimpleFuture<T> implements Future<T> {

  private T result;
  private Throwable cause;
  private boolean hasFailed;
  private boolean complete;
  private Handler<AsyncResult<T>> handler;

  @Override
  public T result() {
    return result;
  }

  @Override
  public Throwable cause() {
    return cause;
  }

  @Override
  public boolean succeeded() {
    return !hasFailed;
  }

  @Override
  public boolean failed() {
    return hasFailed;
  }

  @Override
  public void handle(AsyncResult<T> asyncResult) {

  }

  @Override
  public boolean isComplete() {
    return complete;
  }

  @Override
  public Future<T> setHandler(Handler<AsyncResult<T>> handler) {
    this.handler = handler;
    checkCallHandler();
    return this;
  }

  @Override
  public void complete(T result) {
    complete = true;
    this.result = result;
    checkCallHandler();
  }

  @Override
  public void complete() {
    complete(null);
  }

  @Override
  public void fail(Throwable cause) {
    complete = true;
    hasFailed = true;
    this.cause = cause;
    checkCallHandler();
  }

  @Override
  public void fail(String failureMessage) {
    fail(new Exception(failureMessage));
  }

  @Override
  public boolean tryComplete(T result) {
    return false;
  }

  @Override
  public boolean tryComplete() {
    return false;
  }

  @Override
  public boolean tryFail(Throwable cause) {
    return false;
  }

  @Override
  public boolean tryFail(String failureMessage) {
    return false;
  }

  protected void checkCallHandler() {
    if (handler != null && isComplete()) {
      handler.handle(this);
    }
  }

}
