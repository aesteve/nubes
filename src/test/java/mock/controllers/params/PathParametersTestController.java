package mock.controllers.params;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.PathParam;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

import io.vertx.ext.web.RoutingContext;

@Controller("/params/path/")
public class PathParametersTestController {

    public enum Animal {
        CAT, DOG, LIZARD;
    }

    @Path("string/:parameter")
    public void testParam(RoutingContext context, @PathParam("parameter") String parameter) {
        context.response().end(parameter);
    }

    @Path("enum/:parameter")
    public void testParam(RoutingContext context, @PathParam("parameter") Animal parameter) {
        context.response().end(parameter.toString());
    }

    @Path("long/:parameter")
    public void testParam(RoutingContext context, @PathParam("parameter") Long parameter) {
        context.response().end(parameter.toString());
    }

    @Path("int/:parameter")
    public void testParam(RoutingContext context, @PathParam("parameter") Integer parameter) {
        context.response().end(parameter.toString());
    }
}
