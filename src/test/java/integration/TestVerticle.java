package integration;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.github.aesteve.vertx.nubes.VertxNubes;

import mock.domains.Dog;
import mock.services.DogService;

public class TestVerticle extends AbstractVerticle {

    public static final String HOST = "localhost";
    public static final int PORT = 8000;
    public static final int TIME_FRAME = 10; // We'll sleep through the whole time-frame for testing throttling
    public static final Dog SNOOPY = new Dog("Snoopy", "Beagle");

    public static final DogService dogService = new DogService();

    private VertxNubes mvc;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        HttpServerOptions options = new HttpServerOptions();
        options.setPort(PORT);
        options.setHost(HOST);
        HttpServer server = vertx.createHttpServer(options);
        mvc = new VertxNubes(vertx, createTestConfig());
        mvc.registerService(dogService);
        mvc.registerService(SNOOPY);
        List<Locale> locales = new ArrayList<Locale>();
        locales.add(Locale.FRENCH);
        locales.add(Locale.US);
        locales.add(Locale.JAPANESE);
        locales.add(Locale.ENGLISH);
        mvc.setAvailableLocales(locales);
        mvc.setDefaultLocale(Locale.GERMAN);
        Future<Router> future = Future.future();
        future.setHandler(handler -> {
            if (handler.failed()) {
                startFuture.fail(handler.cause());
            } else {
                Router router = handler.result();
                server.requestHandler(router::accept);
                server.listen();
                System.out.println("Server listening on port : " + PORT);
                startFuture.complete();
            }
        });
        mvc.bootstrap(future);
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        mvc.stop(stopFuture);
    }

    private JsonObject createTestConfig() {
        JsonObject config = new JsonObject();
        JsonArray controllerPackages = new JsonArray();
        controllerPackages.add("mock.controllers");
        config.put("controller-packages", controllerPackages);
        config.put("domain-package", "mock.domains");
        JsonArray fixturePackages = new JsonArray();
        fixturePackages.add("mock.fixtures");
        config.put("fixture-packages", fixturePackages);
        JsonObject throttling = new JsonObject();
        throttling.put("time-frame", TIME_FRAME);
        throttling.put("time-unit", TimeUnit.SECONDS.toString());
        throttling.put("count", 2); // 2 request per 10 seconds
        config.put("throttling", throttling);
        System.out.println("Config : " + config.toString());
        return config;
    }

}
