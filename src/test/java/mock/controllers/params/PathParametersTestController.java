package mock.controllers.params;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Path;
import io.vertx.mvc.annotations.params.PathParameter;
import io.vertx.mvc.controllers.AbstractController;

@Controller
@Path("/params/path/")
public class PathParametersTestController extends AbstractController {

    @Path("dog/:name")
    public void getDogByName(RoutingContext context, @PathParameter("name") String dogName) {
        renderText(context, "My name is : " + dogName);
    }
}
