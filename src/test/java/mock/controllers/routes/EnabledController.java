package mock.controllers.routes;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Disabled;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

@Controller("/enabledController")
public class EnabledController {

    @Path("/disabledRoute")
    @Disabled
    public void disabledRoute(RoutingContext context) {
        throw new RuntimeException("I'm disabled, I should never be called");
    }

    @Path("/enabledRoute")
    public void enabledRoute(RoutingContext context) {
        context.response().end("I'm enabled");
    }
}
