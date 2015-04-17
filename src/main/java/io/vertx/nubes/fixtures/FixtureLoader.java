package io.vertx.nubes.fixtures;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.nubes.Config;
import io.vertx.nubes.services.Service;
import io.vertx.nubes.services.ServiceRegistry;
import io.vertx.nubes.utils.MultipleFutures;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;

public class FixtureLoader {

    public Vertx vertx;
    public Config config;
    public ServiceRegistry serviceRegistry;
    private Set<Fixture> fixtures;

    public Comparator<? extends Fixture> fixtureComparator = (f1, f2) -> {
        return Integer.compare(f1.executionOrder(), f2.executionOrder());
    };

    public FixtureLoader(Vertx vertx, Config config, ServiceRegistry serviceRegistry) {
        this.vertx = vertx;
        this.config = config;
        this.serviceRegistry = serviceRegistry;
        fixtures = new TreeSet<Fixture>();
    }

    public void setUp(Future<Void> future) {
        instanciateFixtures(future);
        if (future.failed()) {
            return;
        }
        exec(future, "startUp");
    }

    public void tearDown(Future<Void> future) {
        exec(future, "tearDown");
    }

    private void exec(Future<Void> startFuture, String methodName) {
        MultipleFutures<Void> futures = new MultipleFutures<Void>();
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
                        throw new IllegalArgumentException("Unknown method : " + methodName);
                }
            } catch (Exception e) {
                fixtureFuture.fail(e);
            }
        }
    }

    private void injectServicesIntoFixture(Fixture fixture) throws IllegalAccessException {
        for (Field field : fixture.getClass().getDeclaredFields()) {
            Service service = serviceRegistry.get(field);
            if (service != null) {
                field.setAccessible(true);
                field.set(fixture, serviceRegistry.get(field));
            }
        }
    }

    private void instanciateFixtures(Future<Void> future) {
        if (config.fixturePackages == null || config.fixturePackages.isEmpty()) {
            return;
        }
        for (String fixturePackage : config.fixturePackages) {
            Reflections reflections = new Reflections(fixturePackage);
            Set<Class<? extends Fixture>> fixtureClasses = reflections.getSubTypesOf(Fixture.class);
            for (Class<? extends Fixture> fixtureClass : fixtureClasses) {
                try {
                    Fixture fixture = fixtureClass.newInstance();
                    injectServicesIntoFixture(fixture);
                    fixtures.add(fixture);
                } catch (Exception e) {
                    future.fail(e);
                    return;
                }
            }
        }

    }

}
