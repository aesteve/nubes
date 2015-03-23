package integration;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.apex.Router;
import io.vertx.mvc.VertxMVC;

public class TestVerticle extends AbstractVerticle {

	public static final String HOST = "localhost";
	public static final int PORT = 8000;
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		HttpServerOptions options = new HttpServerOptions();
		options.setPort(PORT);
		options.setHost(HOST);
		HttpServer server = vertx.createHttpServer(options);
		Router router = VertxMVC.bootstrap(vertx, "mock.controllers");
		System.out.println("Number of routes : "+ router.getRoutes().size());
		server.requestHandler(router::accept);
		server.listen();
		System.out.println("Server listening on port : "+PORT);
		startFuture.complete();
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		stopFuture.complete();
	}

}
