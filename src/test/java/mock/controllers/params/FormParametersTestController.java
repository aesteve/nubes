package mock.controllers.params;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;

import io.vertx.ext.web.RoutingContext;
import mock.domains.Dog;

@Controller("/params/form/")
public class FormParametersTestController {

    public enum Animal {
        CAT, DOG, LIZARD;
    }

    @Path("string")
    @POST
    public void testParam(RoutingContext context, @Param("parameter") String parameter) {
        context.response().end(parameter);
    }

    @Path("dog")
    @POST
    public void testParam(RoutingContext context, @Params Dog dog) {
        context.response().end(dog.toString());
    }
}
