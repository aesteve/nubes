package mock.controllers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.routing.GET;
import io.vertx.nubes.annotations.routing.Path;
import io.vertx.nubes.controllers.AbstractController;

@Controller
public class HelloController extends AbstractController {

    @Path("/hello")
    @GET
    public void sayHello(RoutingContext context) {
        renderText(context, "Hello world!");
    }

}
