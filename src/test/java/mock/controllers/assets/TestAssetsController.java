package mock.controllers.assets;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/assets")
public class TestAssetsController {

	public static final String INSTRUMENT_HEADER = "X-Instrumented";

	@GET("/instrumented*")
	public void instrumentAsset(HttpServerResponse response, RoutingContext context) {
		response.putHeader(INSTRUMENT_HEADER, "yes");
		context.next(); // in this case we HAVE TO since we want to delegate to another handler (out of Nubes scope)
	}
}
