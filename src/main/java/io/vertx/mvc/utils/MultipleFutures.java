package io.vertx.mvc.utils;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

public class MultipleFutures<T> extends SimpleFuture<T> {

	private final List<Future<T>> futures;
	
	public MultipleFutures() {
		this.futures = new ArrayList<Future<T>>();
	}
	
	public void addFuture(Future<T> future) {
		this.futures.add(future);
		future.setHandler(handler -> {
			checkCallHandler();
		});
	}
	
	@Override
	public T result() {
		return null;
	}

	@Override
	public Throwable cause() {
		Exception e = new Exception("At least one future failed");
		futures.forEach(future -> {
			if (future.cause() != null) {
				e.addSuppressed(future.cause());
			}
		});
		return e;
	}

	@Override
	public boolean succeeded() {
		return futures.stream().allMatch(future -> {
			return future.succeeded();
		});
	}

	@Override
	public boolean failed() {
		return futures.stream().anyMatch(future -> {
			return future.failed();
		});
	}

	@Override
	public boolean isComplete() {
		return futures.stream().allMatch(future -> {
			return future.isComplete();
		});
	}
}
