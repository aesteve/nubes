package mock.controllers.params;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/params/query/")
public class QueryParametersTestController {

	public enum Animal {
		CAT, DOG, LIZARD
	}

	@GET("string")
	public void testParam(RoutingContext context, @Param(value = "parameter", mandatory = true) String parameter) {
		context.response().end(parameter);
	}

	@GET("date")
	public void testParam(RoutingContext context, @Param("parameter") Date parameter) {
		context.response().end(Long.toString(parameter.getTime()));
	}

	@GET("enum")
	public void testParam(RoutingContext context, @Param("parameter") Animal parameter) {
		context.response().end(parameter.toString());
	}

	@GET("long")
	public void testParam(RoutingContext context, @Param("parameter") Long parameter) {
		context.response().end(parameter.toString());
	}

	@GET("int")
	public void testParam(RoutingContext context, @Param("parameter") Integer parameter) {
		context.response().end(parameter.toString());
	}

	@GET("float")
	public void testParam(RoutingContext context, @Param("parameter") Float parameter) {
		context.response().end(parameter.toString());
	}

	@GET("byName")
	public void testByName(HttpServerResponse response, @Param String dog) {
		response.end(dog);
	}
}
