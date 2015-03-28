package mock.controllers.params;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Path;
import io.vertx.mvc.annotations.methods.POST;
import io.vertx.mvc.annotations.params.Param;
import io.vertx.mvc.annotations.params.Params;
import io.vertx.mvc.controllers.AbstractController;
import mock.domains.Dog;

@Controller
@Path("/params/form/")
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
