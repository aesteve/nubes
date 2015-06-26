package mock.controllers.redirect;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Redirect;
import com.github.aesteve.vertx.nubes.annotations.routing.Forward;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/redirect")
public class RedirectController {

	public static final String REDIRECT_LOCATION = "http://vertx.io";

	@GET("/client")
	@Redirect(REDIRECT_LOCATION)
	public void redirectMe(RoutingContext context) {
		context.response().putHeader("X-Custom-Header", "method-called");
		context.next();
	}

	@GET("/client/permanent")
	@Redirect(value = REDIRECT_LOCATION, code = 301)
	public void redirectMePermanently(RoutingContext context) {
		context.response().putHeader("X-Custom-Header", "method-called");
		context.next();
	}

	@GET("/server")
	@Forward(controller = AfterRedirectController.class, action = "handleRedirect")
	public void redirectSilently(RoutingContext context) {
		context.response().putHeader("X-Custom-Header", "method-called");
		context.next();
	}
}
