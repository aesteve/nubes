package io.vertx.mvc.fixtures;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.mvc.Config;
import io.vertx.mvc.utils.MultipleFutures;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class FixtureLoader {
	
	public Vertx vertx;
	public Config config;
	
	public FixtureLoader(Vertx vertx, Config config) {
		this.vertx = vertx;
		this.config = config;
	}
	
	public void setUp(Future<Void> future) {
		exec(future, "startUp");
	}
	
	public void tearDown(Future<Void> future) {
		exec(future, "tearDown");
	}	
	
	private void exec(Future<Void> startFuture, String methodName) {
		if (config.fixturePackages == null || config.fixturePackages.isEmpty()) {
			startFuture.complete();
			return;
		}
		MultipleFutures<Void> futures = new MultipleFutures<Void>();
		final Map<Class<? extends Fixture>, Future<Void>> fixtureFutures = new HashMap<Class<? extends Fixture>, Future<Void>>();
		for (String fixturePackage : config.fixturePackages) {
			Reflections reflections = new Reflections(fixturePackage);
			Set<Class<? extends Fixture>> fixtures = reflections.getSubTypesOf(Fixture.class);
			for (Class<? extends Fixture> fixtureClass : fixtures) {
				Future<Void> fixtureFuture = Future.future();
				futures.addFuture(fixtureFuture);
				fixtureFutures.put(fixtureClass, fixtureFuture);
			}
		}
		if (fixtureFutures.isEmpty()) {
			startFuture.complete();
			return;
		}
		futures.setHandler(handler -> {
			if (handler.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(handler.cause());
			}
		});
		for (Class<? extends Fixture> fixtureClass : fixtureFutures.keySet()) {
			Future<Void> fixtureFuture = fixtureFutures.get(fixtureClass);
			try {
				Fixture fixture = fixtureClass.newInstance();
				switch (methodName) {
					case "startUp":
						fixture.startUp(vertx, fixtureFuture);
						break;
					case "tearDown":
						fixture.tearDown(vertx, fixtureFuture);
						break;
					default:
						throw new IllegalArgumentException("Unknown method : "+methodName);
				}
			} catch(Exception e) {
				fixtureFuture.fail(e);
			}
		}
	}
	
}
