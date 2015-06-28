package com.github.aesteve.vertx.nubes.utils.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class AsyncUtils {
	
	public static<T> Handler<AsyncResult<T>> completeOrFail(Future<T> fut) {
		return new CompleteOrFail<>(fut);
	}
	
	public static<T> Handler<AsyncResult<T>> completeFinally(Future<T> fut) {
		return new CompleteFinally<>(fut);
	}
}
