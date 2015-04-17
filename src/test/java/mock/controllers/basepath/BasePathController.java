package mock.controllers.basepath;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.routing.Path;
import io.vertx.nubes.controllers.AbstractController;

@Controller("/base/")
public class BasePathController extends AbstractController {

    @Path("test")
    public void shouldReflectCompletePath(RoutingContext context) {
        renderText(context, "/base/test");
    }
}
