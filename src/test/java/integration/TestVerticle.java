package integration;

import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.completeOrFail;
import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.onSuccessOnly;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.github.aesteve.vertx.nubes.VertxNubes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import mock.auth.MockAuthProvider;
import mock.domains.Dog;
import mock.services.DogService;
import mock.services.impl.ParrotServiceImpl;

public class TestVerticle extends AbstractVerticle {

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

	private VertxNubes nubes;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		HttpServerOptions options = new HttpServerOptions();
		options.setPort(PORT);
		options.setHost(HOST);
		HttpServer server = vertx.createHttpServer(options);
		JsonObject config = createTestConfig();
		nubes = new VertxNubes(vertx, config);
		nubes.registerService(DOG_SERVICE_NAME, dogService);
		nubes.registerService(SNOOPY_SERVICE_NAME, SNOOPY);
		nubes.registerServiceProxy(new ParrotServiceImpl());
		List<Locale> locales = new ArrayList<>();
		locales.add(Locale.FRENCH);
		locales.add(Locale.US);
		locales.add(Locale.JAPANESE);
		locales.add(Locale.ENGLISH);
		nubes.setAvailableLocales(locales);
		nubes.setDefaultLocale(Locale.GERMAN);
		nubes.setAuthProvider(new MockAuthProvider());
		nubes.registerInterceptor("setDateBefore", context -> {
			context.response().headers().add("X-Date-Before", Long.toString(new Date().getTime()));
			context.next();
		});
		nubes.registerInterceptor("setDateAfter", context -> {
			context.response().headers().add("X-Date-After", Long.toString(new Date().getTime()));
			context.next();
		});
		nubes.registerTemplateEngine("hbs", HandlebarsTemplateEngine.create());
		nubes.bootstrap(onSuccessOnly(startFuture, router -> {
			server.requestHandler(router::accept);
			server.listen();
			startFuture.complete();
		}));
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		nubes.stop(completeOrFail(stopFuture));
	}

	private static JsonObject createTestConfig() {
		JsonObject config = new JsonObject();
		JsonArray controllerPackages = new JsonArray();
		controllerPackages.add("mock.controllers");
		config.put("controller-packages", controllerPackages);
		config.put("domain-package", "mock.domains");
		config.put("verticle-package", "mock.verticles");
		JsonArray fixturePackages = new JsonArray();
		fixturePackages.add("mock.fixtures");
		config.put("fixture-packages", fixturePackages);
		JsonObject throttling = new JsonObject();
		throttling.put("time-frame", TIME_FRAME);
		throttling.put("time-unit", TimeUnit.SECONDS.toString());
		throttling.put("count", 2); // 2 request per 10 seconds
		config.put("throttling", throttling);
		config.put("display-errors", true);
		return config;
	}

}
