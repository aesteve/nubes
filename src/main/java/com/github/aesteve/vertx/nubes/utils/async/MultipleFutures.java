package com.github.aesteve.vertx.nubes.utils.async;

import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.completeOrFail;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MultipleFutures<T> extends SimpleFuture<T> {

	private final Map<Handler<Future<T>>, Future<T>> consumers;
	private static final Logger log = LoggerFactory.getLogger(MultipleFutures.class);

	public MultipleFutures() {
		consumers = new HashMap<>();
	}

	public MultipleFutures(Future<T> after) {
		this();
		join(after);
	}

	public MultipleFutures(Handler<AsyncResult<T>> after) {
		this();
		join(after);
	}

	public void add(Handler<Future<T>> handler) {
		Future<T> future = Future.future();
		future.setHandler(futureHandler -> {
			checkCallHandler();
		});
		consumers.put(handler, future);
	}

	public void start() {
		if (consumers.isEmpty()) {
			complete();
			return;
		}
		consumers.forEach((consumer, future) -> {
			consumer.handle(future);
		});
	}

	@Override
	public T result() {
		return null;
	}

	@Override
	public Throwable cause() {
		Exception e = new Exception("At least one future failed");
		consumers.forEach((consumer, future) -> {
			if (future.cause() != null) {
				log.error(future.cause());
				if (e.getCause() == null) {
					e.initCause(future.cause());
				} else {
					e.addSuppressed(future.cause());
				}
			}
		});
		return e;
	}

	@Override
	public boolean succeeded() {
		return consumers.values().stream().allMatch(future -> {
			return future.succeeded();
		});
	}

	@Override
	public boolean failed() {
		return consumers.values().stream().anyMatch(future -> {
			return future.failed();
		});
	}

	@Override
	public boolean isComplete() {
		if (super.isComplete()) { // has been marked explicitly
			return true;
		}
		if (consumers.isEmpty()) {
			return false;
		}
		return consumers.values().stream().allMatch(future -> {
			return future.isComplete();
		});
	}

	public void join(Future<T> future) {
		setHandler(completeOrFail(future));
	}

	public void join(Handler<AsyncResult<T>> handler) {
		setHandler(handler);
	}
}
