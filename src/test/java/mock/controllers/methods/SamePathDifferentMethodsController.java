package mock.controllers.methods;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.annotations.routing.http.DELETE;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.OPTIONS;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PUT;

import io.vertx.ext.web.RoutingContext;

@Controller("/testmethods/")
public class SamePathDifferentMethodsController {

    public final static String PATH = "singleRoute";

    @Path(PATH)
    @GET
    public void get(RoutingContext context) {
        context.response().end("GET");
    }

    @Path(PATH)
    @POST
    public void post(RoutingContext context) {
        context.response().end("POST");
    }

    @Path(PATH)
    @PUT
    public void put(RoutingContext context) {
        context.response().end("PUT");
    }

    @Path(PATH)
    @OPTIONS
    public void options(RoutingContext context) {
        context.response().end("OPTIONS");
    }

    @Path(PATH)
    @DELETE
    public void delete(RoutingContext context) {
        context.response().end("DELETE");
    }

}
