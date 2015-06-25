package mock.controllers.redirect;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.ClientRedirect;
import com.github.aesteve.vertx.nubes.annotations.routing.ServerRedirect;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/redirect")
public class RedirectController {

	@GET("/client")
	@ClientRedirect("http://vertx.io")
	public void redirectMe(RoutingContext context) {
		context.response().putHeader("X-Custom-Header", "method-called");
		context.next();
	}

	@GET("/server")
	@ServerRedirect(controller = AfterRedirectController.class, action = "handleRedirect")
	public void redirectSilently(RoutingContext context) {
		context.response().putHeader("X-Custom-Header", "method-called");
		context.next();
	}
}
