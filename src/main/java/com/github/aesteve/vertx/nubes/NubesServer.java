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

<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Date;

=======
>>>>>>> Added NubesServer  - Tested in a use case which will be committed later
=======
import java.util.Date;

>>>>>>> NubesServer test class implemented
import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.*;

public class NubesServer extends AbstractVerticle {



	private static final Logger log = LoggerFactory.getLogger(NubesServer.class);

	private HttpServer server;
<<<<<<< HEAD
<<<<<<< HEAD
	public static HttpServerOptions options;
	private VertxNubes nubes;
	public static JsonArray services = new JsonArray();
	public static JsonArray templates = new JsonArray();
=======
	private HttpServerOptions options;
	private VertxNubes nubes;
	private JsonArray services = new JsonArray();
	private JsonArray templates = new JsonArray();
>>>>>>> Added NubesServer  - Tested in a use case which will be committed later
=======
	public static HttpServerOptions options;
	private VertxNubes nubes;
	public static JsonArray services = new JsonArray();
	public static JsonArray templates = new JsonArray();
>>>>>>> NubesServer test class implemented
	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		JsonObject config = context.config();
		options = new HttpServerOptions();
		options.setHost(config.getString("host", "localhost"));
		options.setPort(config.getInteger("port", 9000));
		services = config.getJsonArray("services");
<<<<<<< HEAD
<<<<<<< HEAD
		templates = config.getJsonArray("templates",new JsonArray());
		createNubesConfig(config);
		try {
			nubes = new VertxNubes(vertx, config);
=======
		templates = config.getJsonArray("templates");
		JsonObject nubesConfig = createNubesConfig(config);
		try {
			nubes = new VertxNubes(vertx, nubesConfig);
>>>>>>> Added NubesServer  - Tested in a use case which will be committed later
=======
		templates = config.getJsonArray("templates",new JsonArray());

		try {
			nubes = new VertxNubes(vertx, config);
>>>>>>> Modified NubesServer config creation

			//Register services added in conf.json
			for (int i = 0;i<services.size();i++){
				JsonArray tmpService = services.getJsonArray(i);
				String name = tmpService.getString(0);
				String className = tmpService.getString(1);
				Class<?> clazz = Class.forName(className);
				nubes.registerService(name, clazz.newInstance());
			}

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> NubesServer test class implemented
			nubes.registerInterceptor("setDateBefore", contxt -> {
				contxt.response().headers().add("X-Date-Before", Long.toString(new Date().getTime()));
				contxt.next();
			});
			nubes.registerInterceptor("setDateAfter", contxt -> {
				contxt.response().headers().add("X-Date-After", Long.toString(new Date().getTime()));
				contxt.next();
			});
<<<<<<< HEAD
=======
>>>>>>> Added NubesServer  - Tested in a use case which will be committed later
=======
>>>>>>> NubesServer test class implemented
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
<<<<<<< HEAD
<<<<<<< HEAD
			if(!services.isEmpty()){
				services.clear();
			}
=======
>>>>>>> Added NubesServer  - Tested in a use case which will be committed later
=======
			if(!services.isEmpty()){
				services.clear();
			}
>>>>>>> NubesServer test class implemented
		} else {
			future.complete();
		}
	}

<<<<<<< HEAD
	//get the packages paths from conf
	// defaults are: src.package.verticles, src.package.controllers...
<<<<<<< HEAD
<<<<<<< HEAD
	private void createNubesConfig(JsonObject conf) {

		String srcPackage = conf.getString("src-package","src.package");

		if (conf.getString("verticle-package")==null) {
			conf.put("verticle-package", srcPackage + ".verticles");
		}

		if (conf.getString("domain-package")==null) {
			//conf.put("domain-package", srcPackage + ".domains"); still jaxb.index issue
		}

		if (conf.getJsonArray("controller-packages")==null) {
			JsonArray controllers = new JsonArray();
			controllers.add(srcPackage + ".controllers");
			conf.put("controller-packages", controllers);
		}

		if (conf.getJsonArray("fixture-packages")==null){
			JsonArray fixtures = new JsonArray();
			fixtures.add(srcPackage + ".fixtures");
			conf.put("fixture-packages", fixtures);
		}
=======
	private JsonObject createNubesConfig(JsonObject conf) {
		JsonObject json = new JsonObject();
		String srcPackage = conf.getString("src-package","src.package");
		json.put("verticle-package", conf.getString("verticle-package",srcPackage + ".verticles"));
//		json.put("domain-package", conf.getString("domain-package",srcPackage + ".domains")); still jaxb.index issue
		JsonArray fixtures = new JsonArray();
		fixtures.add(srcPackage + ".fixtures");
		json.put("fixture-packages", conf.getJsonArray("fixture-packages", fixtures));
		JsonArray controllers = new JsonArray();
		controllers.add(srcPackage + ".controllers");
		json.put("controller-packages", conf.getJsonArray("controller-packages",controllers));
		return json;
>>>>>>> Added NubesServer  - Tested in a use case which will be committed later
=======
	private void createNubesConfig(JsonObject conf) {

		String srcPackage = conf.getString("src-package","src.package");

		if (conf.getString("verticle-package")==null) {
			conf.put("verticle-package", srcPackage + ".verticles");
		}

		if (conf.getString("domain-package")==null) {
			//conf.put("domain-package", srcPackage + ".domains"); still jaxb.index issue
		}

		if (conf.getJsonArray("controller-packages")==null) {
			JsonArray controllers = new JsonArray();
			controllers.add(srcPackage + ".controllers");
			conf.put("controller-packages", controllers);
		}

		if (conf.getJsonArray("fixture-packages")==null){
			JsonArray fixtures = new JsonArray();
			fixtures.add(srcPackage + ".fixtures");
			conf.put("fixture-packages", fixtures);
		}
>>>>>>> Modified NubesServer config creation
	}
=======
>>>>>>> Added src-package to intern Nubes Config
}
