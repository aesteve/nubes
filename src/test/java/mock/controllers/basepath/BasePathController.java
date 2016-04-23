package mock.controllers.basepath;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.ext.web.RoutingContext;

@Controller("/base/")
public class BasePathController {

	@GET("test")
	public void shouldReflectCompletePath(RoutingContext context) {
		context.response().end("/base/test");
	}
}
