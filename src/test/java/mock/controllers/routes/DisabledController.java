package mock.controllers.routes;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Disabled;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

@Controller("/disabledController")
@Disabled
public class DisabledController {

    @Path("/route")
    public void disabledRoute(RoutingContext context) {
        throw new RuntimeException("I'm disabled, I should never be called");
    }
}
