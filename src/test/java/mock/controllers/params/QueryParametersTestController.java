package mock.controllers.params;

import io.vertx.ext.web.RoutingContext;

import java.util.Date;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

@Controller("/params/query/")
public class QueryParametersTestController {

    public enum Animal {
        CAT, DOG, LIZARD;
    }

    @Path("string")
    public void testParam(RoutingContext context, @Param(value = "parameter", mandatory = true) String parameter) {
        context.response().end(parameter);
    }

    @Path("date")
    public void testParam(RoutingContext context, @Param("parameter") Date parameter) {
        context.response().end(Long.toString(parameter.getTime()));
    }

    @Path("enum")
    public void testParam(RoutingContext context, @Param("parameter") Animal parameter) {
        context.response().end(parameter.toString());
    }

    @Path("long")
    public void testParam(RoutingContext context, @Param("parameter") Long parameter) {
        context.response().end(parameter.toString());
    }

    @Path("int")
    public void testParam(RoutingContext context, @Param("parameter") Integer parameter) {
        context.response().end(parameter.toString());
    }

    @Path("float")
    public void testParam(RoutingContext context, @Param("parameter") Float parameter) {
        context.response().end(parameter.toString());
    }
}
