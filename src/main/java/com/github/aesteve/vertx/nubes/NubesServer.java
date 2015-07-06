package com.github.aesteve.vertx.nubes;

import com.github.aesteve.vertx.nubes.exceptions.MissingConfigurationException;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.templ.impl.HandlebarsTemplateEngineImpl;
import io.vertx.ext.web.templ.impl.JadeTemplateEngineImpl;
import io.vertx.ext.web.templ.impl.MVELTemplateEngineImpl;
import io.vertx.ext.web.templ.impl.ThymeleafTemplateEngineImpl;

import java.util.Date;

import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.*;

public class NubesServer extends AbstractVerticle {



	private static final Logger log = LoggerFactory.getLogger(NubesServer.class);

	private HttpServer server;
	public static HttpServerOptions options;
	private VertxNubes nubes;
	public static JsonArray services = new JsonArray();
	public static JsonArray templates = new JsonArray();
	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		JsonObject config = context.config();
		options = new HttpServerOptions();
		options.setHost(config.getString("host", "localhost"));
		options.setPort(config.getInteger("port", 9000));
		services = config.getJsonArray("services");
		templates = config.getJsonArray("templates",new JsonArray());

		try {
			nubes = new VertxNubes(vertx, config);

			//Register services added in conf.json
			for (int i = 0;i<services.size();i++){
				JsonArray tmpService = services.getJsonArray(i);
				String name = tmpService.getString(0);
				String className = tmpService.getString(1);
				Class<?> clazz = Class.forName(className);
				nubes.registerService(name, clazz.newInstance());
			}

			nubes.registerInterceptor("setDateBefore", contxt -> {
				contxt.response().headers().add("X-Date-Before", Long.toString(new Date().getTime()));
				contxt.next();
			});
			nubes.registerInterceptor("setDateAfter", contxt -> {
				contxt.response().headers().add("X-Date-After", Long.toString(new Date().getTime()));
				contxt.next();
			});
			//Register templateEngines for extensions added in conf.json
			if(templates.contains("hbs")) {
				nubes.registerTemplateEngine("hbs", new HandlebarsTemplateEngineImpl());
				log.info("HandlebarsTemplateEngine registered");
			}
			if(templates.contains("jade")) {
				nubes.registerTemplateEngine("jade", new JadeTemplateEngineImpl());
				log.info("JadeTemplateEngine registered");
			}
			if(templates.contains("templ")){
				nubes.registerTemplateEngine("templ", new MVELTemplateEngineImpl());
				log.info("MVELTemplateEngine registered");
			}
			if(templates.contains("thymeleaf")){
				nubes.registerTemplateEngine("html", new ThymeleafTemplateEngineImpl());
				log.info("ThymeleafTemplateEngine registered");
			}

		} catch (MissingConfigurationException me) {
			throw new VertxException(me);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Future<Void> future) {
		server = vertx.createHttpServer(options);
		nubes.bootstrap(onSuccessOnly(future, router -> {
			server.requestHandler(router::accept);
			server.listen(ignoreResult(future));
			log.info("Server listening on port : " + options.getPort());
		}));
	}

	@Override
	public void stop(Future<Void> future) {
		nubes.stop(nubesRes -> closeServer(future));
	}

	private void closeServer(Future<Void> future) {
		if (server != null) {
			server.close(completeOrFail(future));
			if(!services.isEmpty()){
				services.clear();
			}
		} else {
			future.complete();
		}
	}

}
