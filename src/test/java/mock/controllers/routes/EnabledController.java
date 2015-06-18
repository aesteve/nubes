package mock.controllers.routes;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Disabled;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/enabledController")
public class EnabledController {

    @GET("/disabledRoute")
    @Disabled
    public void disabledRoute(RoutingContext context) {
        throw new RuntimeException("I'm disabled, I should never be called");
    }

    @GET("/enabledRoute")
    public void enabledRoute(RoutingContext context) {
        context.response().end("I'm enabled");
    }
}
