package mock.controllers.basepath;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.ext.web.RoutingContext;

@Controller
public class HelloController {

	@GET("/hello")
	public void sayHello(RoutingContext context) {
		context.response().end("Hello world!");
	}

}
