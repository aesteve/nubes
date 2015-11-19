package mock.controllers.routes;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/void")
public class VoidController {

	@GET
	public void doNothing() {}

	@GET("/withResponse")
	public void withResponse(HttpServerResponse response) {
		response.end("Something");
	}

	@GET("/withAsyncResponse")
	public void asyncWithResponse(HttpServerResponse response, Vertx vertx) {
		vertx.setTimer(1000, timer -> response.end());
	}

}
