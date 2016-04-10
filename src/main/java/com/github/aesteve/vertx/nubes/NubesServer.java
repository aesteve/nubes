package com.github.aesteve.vertx.nubes;

import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.completeOrFail;
import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.ignoreResult;
import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.onSuccessOnly;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class NubesServer extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(NubesServer.class);

	protected HttpServer server;
	protected HttpServerOptions options;
	protected VertxNubes nubes;

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		JsonObject config = context.config();
		options = new HttpServerOptions();
		options.setHost(config.getString("host", "localhost"));
		options.setPort(config.getInteger("port", 9000));
		nubes = new VertxNubes(vertx, config);
	}

	@Override
	public void start(Future<Void> future) {
		server = vertx.createHttpServer(options);
		nubes.bootstrap(onSuccessOnly(future, router -> {
			server.requestHandler(router::accept);
			server.listen(ignoreResult(future));
			LOG.info("Server listening on port : " + options.getPort());
		}));
	}

	@Override
	public void stop(Future<Void> future) {
		nubes.stop(nubesRes -> closeServer(future));
	}

	private void closeServer(Future<Void> future) {
		if (server != null) {
			LOG.info("Closing HTTP server");
			server.close(completeOrFail(future));
		} else {
			future.complete();
		}
	}

}
