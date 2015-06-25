package mock.controllers.basepath;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/base/")
public class BasePathController {

	@GET("test")
	public void shouldReflectCompletePath(RoutingContext context) {
		context.response().end("/base/test");
	}
}
