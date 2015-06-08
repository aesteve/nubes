package mock.controllers.params;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.params.Param;
import io.vertx.nubes.annotations.params.Params;
import io.vertx.nubes.annotations.routing.POST;
import io.vertx.nubes.annotations.routing.Path;
import io.vertx.nubes.controllers.AbstractController;
import mock.domains.Dog;

@Controller("/params/form/")
public class FormParametersTestController extends AbstractController {

    public enum Animal {
        CAT, DOG, LIZARD;
    }

    @Path("string")
    @POST
    public void testParam(RoutingContext context, @Param("parameter") String parameter) {
        renderText(context, parameter);
    }

    @Path("dog")
    @POST
    public void testParam(RoutingContext context, @Params Dog dog) {
        renderText(context, dog.toString());
    }
}
