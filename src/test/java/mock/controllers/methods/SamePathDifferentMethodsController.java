package mock.controllers.methods;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.routing.DELETE;
import io.vertx.nubes.annotations.routing.GET;
import io.vertx.nubes.annotations.routing.OPTIONS;
import io.vertx.nubes.annotations.routing.POST;
import io.vertx.nubes.annotations.routing.PUT;
import io.vertx.nubes.annotations.routing.Path;
import io.vertx.nubes.controllers.AbstractController;

@Controller("/testmethods/")
public class SamePathDifferentMethodsController extends AbstractController {

    public final static String PATH = "singleRoute";

    @Path(PATH)
    @GET
    public void get(RoutingContext context) {
        renderText(context, "GET");
    }

    @Path(PATH)
    @POST
    public void post(RoutingContext context) {
        renderText(context, "POST");
    }

    @Path(PATH)
    @PUT
    public void put(RoutingContext context) {
        renderText(context, "PUT");
    }

    @Path(PATH)
    @OPTIONS
    public void options(RoutingContext context) {
        renderText(context, "OPTIONS");
    }

    @Path(PATH)
    @DELETE
    public void delete(RoutingContext context) {
        renderText(context, "DELETE");
    }

}
