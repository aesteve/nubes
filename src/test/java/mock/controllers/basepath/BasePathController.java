package mock.controllers.basepath;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Path;
import io.vertx.mvc.controllers.AbstractController;

@Controller("/base/")
public class BasePathController extends AbstractController {

    @Path("test")
    public void shouldReflectCompletePath(RoutingContext context) {
        renderText(context, "/base/test");
    }
}
