package mock.custom.controllers;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.utils.DateUtils;
import io.vertx.ext.web.RoutingContext;

@Controller("/custom/paramHandler")
public class ParamHandlerControllerTest {
	
	@GET
	public void testParamHandler(DateUtils utils, RoutingContext ctx) {
		ctx.response().end();
	}
}
