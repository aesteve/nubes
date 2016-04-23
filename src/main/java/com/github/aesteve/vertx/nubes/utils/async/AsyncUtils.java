package com.github.aesteve.vertx.nubes.utils.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsyncUtils {

  private static final Logger LOG = LoggerFactory.getLogger(AsyncUtils.class);

  public static <T> Handler<AsyncResult<T>> completeFinally(Future<T> fut) {
    return (res -> fut.complete());
  }

  public static <T> Handler<AsyncResult<T>> completeOrFail(Future<T> fut) {
    return res -> {
      if (res.failed()) {
        fut.fail(res.cause());
      } else {
        fut.complete(res.result());
      }
    };
  }

  public static <T> Handler<AsyncResult<T>> ignoreResult(Future<Void> future) {
    return res -> {
      if (res.failed()) {
        future.fail(res.cause());
      } else {
        future.complete();
      }
    };
  }

  public static <T> AsyncResult<Void> withoutResult(AsyncResult<T> res) {
    if (res.failed()) {
      return Future.failedFuture(res.cause());
    } else {
      return Future.succeededFuture();
    }
  }

  public static <T> Handler<AsyncResult<T>> onSuccessOnly(Future<Void> future, Handler<T> handler) {
    return res -> {
      if (res.failed()) {
        future.fail(res.cause());
        return;
      }
      handler.handle(res.result());
    };
  }

  public static <T> Handler<AsyncResult<T>> onSuccessOnly(NoArgHandler block) {
    return res -> {
      if (res.failed()) {
        LOG.warn("Exception has been swallowed by AsyncUtils.onSuccess", res.cause());
        return;
      }
      block.handle();
    };
  }

  public static <T> Handler<AsyncResult<T>> onSuccessOnly(Handler<T> handler) {
    return res -> {
      if (res.failed()) {
        LOG.warn("Exception has been swallowed by AsyncUtils.onSuccess", res.cause());
        return;
      }
      handler.handle(res.result());
    };
  }

  public static <T> Handler<AsyncResult<T>> onFailureOnly(Handler<T> handler) {
    return res -> {
      if (res.failed()) {
        handler.handle(res.result());
      }
    };
  }

  public static <T> Handler<AsyncResult<T>> nextOrFail(RoutingContext context) {
    return res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        context.next();
      }
    };
  }

  public static <T> Handler<AsyncResult<T>> failOr(RoutingContext context, Handler<AsyncResult<T>> handler) {
    return res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        handler.handle(res);
      }
    };
  }

  public static <T, U, V> Future<V> chainOnSuccess(Handler<AsyncResult<T>> globalHandler, Future<U> future, Handler<Future<V>> nextHandler) {
    Future<V> nextFuture = Future.future();
    future.setHandler(res -> {
      if (res.failed()) {
        globalHandler.handle(Future.failedFuture(res.cause()));
      } else {
        nextHandler.handle(nextFuture);
      }
    });
    return nextFuture;
  }

  public static <T> void chainHandlers(Handler<AsyncResult<T>> global, List<Handler<Future<T>>> handlers) {
    if (handlers == null || handlers.isEmpty()) {
      global.handle(Future.succeededFuture());
      return;
    }
    int nbHandlers = handlers.size();
    Future<T> firstFuture = Future.future();
    if (nbHandlers == 1) {
      firstFuture.setHandler(global);
      handlers.get(0).handle(firstFuture);
      return;
    }
    List<Future<T>> futures = new ArrayList<>(handlers.size());
    futures.add(firstFuture);
    int i = 0;
    for (Handler<Future<T>> handler : handlers) {
      if (i > 0) {
        Future<T> fut = futures.get(i - 1);
        futures.add(chainOnSuccess(global, fut, handler));
      }
      i++;
    }
    futures.get(futures.size() - 1).setHandler(global);
    handlers.get(0).handle(firstFuture);
  }

  public static <T> void chainHandlers(Future<T> global, List<Handler<Future<T>>> handlers) {
    chainHandlers(res -> {
      if (res.failed()) {
        global.fail(res.cause());
      } else {
        global.complete(res.result());
      }
    }, handlers);
  }

  public static <T, U> Future<Void> chainOnSuccess(Handler<AsyncResult<T>> globalHandler, Future<U> future, List<Handler<Future<Void>>> list) {
    List<Future<Void>> futures = new ArrayList<>(list.size());
    int i = 0;
    Future<Void> firstFuture = Future.future();
    for (Handler<Future<Void>> handler : list) {
      Future<Void> fut = i == 0 ? firstFuture : futures.get(i - 1);
      futures.add(chainOnSuccess(globalHandler, fut, handler));
      i++;
    }
    future.setHandler(res -> {
      if (res.failed()) {
        globalHandler.handle(Future.failedFuture(res.cause()));
      } else {
        list.get(0).handle(firstFuture);
      }
    });
    return futures.get(futures.size() - 1);
  }

  @SafeVarargs
  // or we're screwed...
  public static <T, U> Future<Void> chainOnSuccess(Handler<AsyncResult<T>> globalHandler, Future<U> future, Handler<Future<Void>>... handlers) {
    List<Handler<Future<Void>>> list = Arrays.asList(handlers);
    return AsyncUtils.chainOnSuccess(globalHandler, future, list);
  }

  public static <T> Handler<AsyncResult<T>> logIfFailed(final String msg, final Logger LOG) {
    return res -> {
      if (res.failed()) {
        if (msg != null) {
          LOG.error(msg, res.cause());
        } else {
          LOG.error(res.cause());
        }
      }
    };
  }
}
