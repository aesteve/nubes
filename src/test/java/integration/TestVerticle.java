package integration;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.apex.Router;
import io.vertx.mvc.VertxMVC;

import java.util.concurrent.TimeUnit;

public class TestVerticle extends AbstractVerticle {

	public static final String HOST = "localhost";
	public static final int PORT = 8000;
	public static final int TIME_FRAME = 10; // We'll sleep through the whole time-frame for testing throttling
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		HttpServerOptions options = new HttpServerOptions();
		options.setPort(PORT);
		options.setHost(HOST);
		HttpServer server = vertx.createHttpServer(options);
		VertxMVC mvc = new VertxMVC(vertx, createTestConfig());
		Router router = mvc.bootstrap();
		System.out.println("Number of routes : "+ router.getRoutes().size());
		server.requestHandler(router::accept);
		server.listen();
		System.out.println("Server listening on port : "+PORT);
		startFuture.complete();
	}
	
	private JsonObject createTestConfig() {
		JsonObject config = new JsonObject();
		JsonArray controllerPackages = new JsonArray();
		controllerPackages.add("mock.controllers");
		config.put("controller-packages", controllerPackages);
		JsonObject throttling = new JsonObject();
		throttling.put("time-frame", TIME_FRAME);
		throttling.put("time-unit", TimeUnit.SECONDS.toString());
		throttling.put("count", 2); // 2 request per 10 seconds
		config.put("throttling", throttling);
		System.out.println("Config : "+config.toString());
		return config;
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		stopFuture.complete();
	}

}
