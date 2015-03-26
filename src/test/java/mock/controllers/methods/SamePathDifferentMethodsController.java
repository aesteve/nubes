package mock.controllers.methods;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Path;
import io.vertx.mvc.annotations.methods.DELETE;
import io.vertx.mvc.annotations.methods.GET;
import io.vertx.mvc.annotations.methods.OPTIONS;
import io.vertx.mvc.annotations.methods.POST;
import io.vertx.mvc.annotations.methods.PUT;
import io.vertx.mvc.controllers.AbstractController;

@Controller
@Path("/testmethods/")
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
