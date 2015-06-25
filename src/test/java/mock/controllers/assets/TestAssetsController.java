package mock.controllers.assets;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/assets")
public class TestAssetsController {

	public static final String INSTRUMENT_HEADER = "X-Instrumented";

	@GET("/instrumented*")
	public void instrumentAsset(RoutingContext context) {
		context.response().headers().add(INSTRUMENT_HEADER, "yes");
		context.next();
	}
}
