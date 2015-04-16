package mock.controllers.redirect;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.routing.ClientRedirect;
import io.vertx.mvc.annotations.routing.Path;
import io.vertx.mvc.annotations.routing.ServerRedirect;

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
