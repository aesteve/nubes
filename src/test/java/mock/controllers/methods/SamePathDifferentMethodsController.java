package mock.controllers.methods;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.DELETE;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.OPTIONS;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PATCH;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PUT;

@Controller("/testmethods/")
public class SamePathDifferentMethodsController {

	public final static String PATH = "singleRoute";

	@GET(PATH)
	public void get(RoutingContext context) {
		context.response().end("GET");
	}

	@POST(PATH)
	public void post(RoutingContext context) {
		context.response().end("POST");
	}

	@PUT(PATH)
	@PATCH(PATH)
	public void put(RoutingContext context) {
		context.response().end("PUT");
	}

	@OPTIONS(PATH)
	public void options(RoutingContext context) {
		context.response().end("OPTIONS");
	}

	@DELETE(PATH)
	public void delete(RoutingContext context) {
		context.response().end("DELETE");
	}

}
