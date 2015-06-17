package integration;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import mock.auth.MockAuthProvider;
import mock.domains.Dog;
import mock.services.DogService;

import com.github.aesteve.vertx.nubes.VertxNubes;

public class TestVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(TestVerticle.class);

    public static final String HOST = "localhost";
    public static final int PORT = 8000;
    public static final int TIME_FRAME = 10; // We'll sleep through the whole
                                             // time-frame for testing
                                             // throttling
    public static final Dog SNOOPY = new Dog("Snoopy", "Beagle");
    public static final DogService dogService = new DogService();
    public static final String HEADER_DATE_BEFORE = "X-Date-Before";
    public static final String HEADER_DATE_AFTER = "X-Date-After";

    public static final String DOG_SERVICE_NAME = "dogService";
    public static final String SNOOPY_SERVICE_NAME = "snoopy";

    private VertxNubes mvc;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        HttpServerOptions options = new HttpServerOptions();
        options.setPort(PORT);
        options.setHost(HOST);
        HttpServer server = vertx.createHttpServer(options);
        mvc = new VertxNubes(vertx, createTestConfig());
        mvc.registerService(DOG_SERVICE_NAME, dogService);
        mvc.registerService(SNOOPY_SERVICE_NAME, SNOOPY);
        List<Locale> locales = new ArrayList<Locale>();
        locales.add(Locale.FRENCH);
        locales.add(Locale.US);
        locales.add(Locale.JAPANESE);
        locales.add(Locale.ENGLISH);
        mvc.setAvailableLocales(locales);
        mvc.setDefaultLocale(Locale.GERMAN);
        mvc.setAuthProvider(new MockAuthProvider());
        mvc.registerInterceptor("setDateBefore", context -> {
            context.response().headers().add("X-Date-Before", Long.toString(new Date().getTime()));
            context.next();
        });
        mvc.registerInterceptor("setDateAfter", context -> {
            context.response().headers().add("X-Date-After", Long.toString(new Date().getTime()));
            context.next();
        });
        mvc.bootstrap(res -> {
            if (res.failed()) {
                startFuture.fail(res.cause());
            } else {
                Router router = res.result();
                server.requestHandler(router::accept);
                server.listen();
                log.info("Server listening on port : " + PORT);
                startFuture.complete();
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        mvc.stop(res -> {
            if (res.succeeded()) {
                stopFuture.complete();
            } else {
                stopFuture.fail(res.cause());
            }
        });
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
        log.info("Config : " + config.toString());
        return config;
    }

}
