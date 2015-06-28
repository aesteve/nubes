package com.github.aesteve.vertx.nubes.utils.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class CompleteOrFail<T> implements Handler<AsyncResult<T>>{
	
	private Future<T> future;
	
	public CompleteOrFail(Future<T> future) {
		this.future = future;
	}

	@Override
	public void handle(AsyncResult<T> async) {
		if (async.failed()) {
			future.fail(async.cause());
			return;
		}
		future.complete(async.result());
	}
	
}
