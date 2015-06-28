package com.github.aesteve.vertx.nubes.utils.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CompleteFinally<T> implements Handler<AsyncResult<T>>{
	
	private Future<T> future;
	private final static Logger log = LoggerFactory.getLogger(CompleteFinally.class);
	
	public CompleteFinally(Future<T> future) {
		this.future = future;
	}

	@Override
	public void handle(AsyncResult<T> async) {
		if (async.failed()) {
			log.error("Async result failed", async.cause());
		}
		future.complete(async.result());
	}
	
}
