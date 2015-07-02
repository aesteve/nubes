package mock.controllers.redirect;

import io.vertx.core.http.HttpServerResponse;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Forward;
import com.github.aesteve.vertx.nubes.annotations.routing.Redirect;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/redirect")
public class RedirectController {

	public static final String REDIRECT_LOCATION = "http://vertx.io";

	@GET("/client")
	@Redirect(REDIRECT_LOCATION)
	public void redirectMe(HttpServerResponse response) {
		response.putHeader("X-Custom-Header", "method-called");
	}

	@GET("/client/permanent")
	@Redirect(value = REDIRECT_LOCATION, code = 301)
	public void redirectMePermanently(HttpServerResponse response) {
		response.putHeader("X-Custom-Header", "method-called");
	}

	@GET("/server")
	@Forward(controller = AfterRedirectController.class, action = "handleRedirect")
	public void redirectSilently(HttpServerResponse response) {
		response.putHeader("X-Custom-Header", "method-called");
	}
}
