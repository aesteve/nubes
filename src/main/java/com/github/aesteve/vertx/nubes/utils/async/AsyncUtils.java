package com.github.aesteve.vertx.nubes.utils.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class AsyncUtils {
	
	private static final Logger log = LoggerFactory.getLogger(AsyncUtils.class);
	
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
	
	public static<T> Handler<AsyncResult<T>> onSuccessOnly(Handler<T> handler) {
		return (res -> {
			if (res.failed()) {
				log.warn("Exception has been swallowed by AsyncUtils.onSuccess", res.cause());
				return;
			}
			handler.handle(res.result());
		});
	}

	public static<T> Handler<AsyncResult<T>> onFailureOnly(Handler<T> handler) {
		return (res -> {
			if (res.failed()) {
				handler.handle(res.result());
			}
		});
	}

}
