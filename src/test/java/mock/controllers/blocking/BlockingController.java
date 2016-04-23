package mock.controllers.blocking;

import com.github.aesteve.vertx.nubes.annotations.Blocking;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.ext.web.RoutingContext;

@Controller("/blocking")
public class BlockingController {
	@GET("/test")
	@Blocking
	public void test(RoutingContext context) throws Exception {
		Thread.sleep(3000);
		context.response().end("Done.");
	}
}
