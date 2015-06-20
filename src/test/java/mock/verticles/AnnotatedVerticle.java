package mock.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.aesteve.vertx.nubes.annotations.services.Verticle;

@Verticle(instances = 1)
public class AnnotatedVerticle extends AbstractVerticle {

	public static AtomicInteger nbInstances = new AtomicInteger();
	public static AtomicBoolean isStarted = new AtomicBoolean();

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		nbInstances.incrementAndGet();
	}

	@Override
	public void start(Future<Void> future) {
		isStarted.set(true);
		future.complete();
	}

	@Override
	public void stop(Future<Void> future) {
		isStarted.set(false);
		future.complete();
	}

}
