package mock.controllers.params;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/params/path/")
public class PathParametersTestController {

	public enum Animal {
		CAT, DOG, LIZARD
	}

	@GET("string/:parameter")
	public void testParam(RoutingContext context, @Param("parameter") String parameter) {
		context.response().end(parameter);
	}

	@GET("enum/:parameter")
	public void testParam(RoutingContext context, @Param("parameter") Animal parameter) {
		context.response().end(parameter.toString());
	}

	@GET("long/:parameter")
	public void testParam(RoutingContext context, @Param("parameter") Long parameter) {
		context.response().end(parameter.toString());
	}

	@GET("int/:parameter")
	public void testParam(RoutingContext context, @Param("parameter") Integer parameter) {
		context.response().end(parameter.toString());
	}

	@GET("byName/:dog")
	public void testParamByName(HttpServerResponse response, @Param String dog) {
		response.end(dog);
	}
}
