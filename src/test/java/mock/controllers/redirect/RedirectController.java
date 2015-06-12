package mock.controllers.redirect;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.ClientRedirect;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.annotations.routing.ServerRedirect;

import io.vertx.ext.web.RoutingContext;

@Controller("/redirect")
public class RedirectController {

    @Path("/client")
    @ClientRedirect("http://vertx.io")
    public void redirectMe(RoutingContext context) {
        context.response().putHeader("X-Custom-Header", "method-called");
        context.next();
    }

    @Path("/server")
    @ServerRedirect(controller = AfterRedirectController.class, action = "handleRedirect")
    public void redirectSilently(RoutingContext context) {
        context.response().putHeader("X-Custom-Header", "method-called");
        context.next();
    }
}
