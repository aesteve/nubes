package io.vertx.mvc.fixtures;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.mvc.Config;
import io.vertx.mvc.utils.MultipleFutures;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;

public class FixtureLoader {
	
	public Vertx vertx;
	public Config config;
	public Comparator<? extends Fixture> fixtureComparator = (f1, f2) -> {
		return Integer.compare(f1.executionOrder(), f2.executionOrder());
	};
	
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
		Set<Fixture> fixtures = new TreeSet<Fixture>();
		for (String fixturePackage : config.fixturePackages) {
			Reflections reflections = new Reflections(fixturePackage);
			Set<Class<? extends Fixture>> fixtureClasses = reflections.getSubTypesOf(Fixture.class);
			for (Class<? extends Fixture> fixtureClass : fixtureClasses) {
				try {
					Fixture fixture = fixtureClass.newInstance();
					fixtures.add(fixture);
				} catch(Exception e) {
					startFuture.fail(e);
					return;
				}
			}
		}
		if (fixtures.isEmpty()) {
			startFuture.complete();
			return;
		}
		final Map<Fixture, Future<Void>> fixtureFutures = new LinkedHashMap<Fixture, Future<Void>>();
		for (Fixture fixture : fixtures) {
			Future<Void> fixtureFuture = Future.future();
			futures.addFuture(fixtureFuture);
			fixtureFutures.put(fixture, fixtureFuture);
		}
		futures.setHandler(handler -> {
			if (handler.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(handler.cause());
			}
		});
		for (Fixture fixture : fixtureFutures.keySet()) {
			Future<Void> fixtureFuture = fixtureFutures.get(fixture);
			try {
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
