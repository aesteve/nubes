package mock.controllers.basepath;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

import io.vertx.ext.web.RoutingContext;

@Controller("/base/")
public class BasePathController {

    @Path("test")
    public void shouldReflectCompletePath(RoutingContext context) {
        context.response().end("/base/test");
    }
}
