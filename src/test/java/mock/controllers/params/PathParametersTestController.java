package mock.controllers.params;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.params.PathParam;
import io.vertx.nubes.annotations.routing.Path;
import io.vertx.nubes.controllers.AbstractController;

@Controller("/params/path/")
public class PathParametersTestController extends AbstractController {

    public enum Animal {
        CAT, DOG, LIZARD;
    }

    @Path("string/:parameter")
    public void testParam(RoutingContext context, @PathParam("parameter") String parameter) {
        renderText(context, parameter);
    }

    @Path("enum/:parameter")
    public void testParam(RoutingContext context, @PathParam("parameter") Animal parameter) {
        renderText(context, parameter.toString());
    }

    @Path("long/:parameter")
    public void testParam(RoutingContext context, @PathParam("parameter") Long parameter) {
        renderText(context, parameter.toString());
    }

    @Path("int/:parameter")
    public void testParam(RoutingContext context, @PathParam("parameter") Integer parameter) {
        renderText(context, parameter.toString());
    }
}
