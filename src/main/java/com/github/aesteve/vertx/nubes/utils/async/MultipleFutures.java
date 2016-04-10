package com.github.aesteve.vertx.nubes.utils.async;

import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.completeOrFail;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.github.aesteve.vertx.nubes.utils.functional.TriConsumer;

public class MultipleFutures<T> extends SimpleFuture<T> {

	private final Map<Handler<Future<T>>, Future<T>> consumers;
	private static final Logger LOG = LoggerFactory.getLogger(MultipleFutures.class);

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

	public <L> MultipleFutures(Collection<L> elements, Function<L, Handler<Future<T>>> transform) {
		this();
		addAll(elements, transform);
	}

	public <L> MultipleFutures(Collection<L> elements, BiConsumer<L, Future<T>> transform) {
		this();
		addAll(elements, transform);
	}

	public <K, V> MultipleFutures(Map<K, V> elements, TriConsumer<K, V, Future<T>> transform) {
		this();
		addAll(elements, transform);
	}

	//

	public MultipleFutures<T> add(Handler<Future<T>> handler) {
		if (handler == null) {
			return this;
		}
		Future<T> future = Future.future();
		future.setHandler(futureHandler -> checkCallHandler());
		consumers.put(handler, future);
		return this;
	}

	public MultipleFutures<T> start() {
		if (consumers.isEmpty()) {
			complete();
			return this;
		}
		consumers.forEach(Handler::handle);
		return this;
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
				LOG.error(future.cause());
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
		return consumers.values().stream().allMatch(Future::succeeded);
	}

	@Override
	public boolean failed() {
		return consumers.values().stream().anyMatch(Future::failed);
	}

	@Override
	public boolean isComplete() {
		if (super.isComplete()) { // has been marked explicitly
			return true;
		}
		return !consumers.isEmpty() &&
				consumers.values().stream().allMatch(Future::isComplete);
	}

	public MultipleFutures<T> join(Future<T> future) {
		setHandler(completeOrFail(future));
		return this;
	}

	public MultipleFutures<T> join(Handler<AsyncResult<T>> handler) {
		setHandler(handler);
		return this;
	}

	public <L> MultipleFutures<T> addAll(Collection<L> list, BiConsumer<L, Future<T>> transform) {
		list.forEach(element -> add(res -> transform.accept(element, res)));
		return this;
	}

	public <L> MultipleFutures<T> addAll(Collection<L> list, Function<L, Handler<Future<T>>> transform) {
		list.forEach(element -> add(transform.apply(element)));
		return this;
	}

	public <L> MultipleFutures<T> treatAllNow(Collection<L> list, Function<L, Handler<Future<T>>> transform) {
		addAll(list, transform);
		start();
		return this;
	}

	public <K, V> MultipleFutures<T> addAll(Map<K, V> map, TriConsumer<K, V, Future<T>> transform) {
		map.forEach((key, value) -> add(res -> {
			transform.accept(key, value, res);
		}));
		return this;
	}

}
