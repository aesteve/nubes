package com.github.aesteve.vertx.nubes.utils.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class AsyncUtils {
	
	public static<T> Handler<AsyncResult<T>> completeOrFail(Future<T> fut) {
		return (res -> {
			if (res.failed()) {
				fut.fail(res.cause());
			} else {
				fut.complete(res.result());
			}
		});
	}
	
	public static<T> Handler<AsyncResult<T>> completeFinally(Future<T> fut) {
		return (res -> {
			fut.complete();
		});
	}
	
	public static<T> Handler<AsyncResult<T>> ignoreResult(Future<Void> future) {
		return (res -> {
			if (res.failed()) {
				future.fail(res.cause()); 
			} else {
				future.complete();
			}
		});
	}
}
