package mock.controllers.routes;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Disabled;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.ext.web.RoutingContext;

@Controller("/disabledController")
@Disabled
public class DisabledController {

	@GET("/route")
	public void disabledRoute(RoutingContext context) {
		throw new RuntimeException("I'm disabled, I should never be called");
	}
}
