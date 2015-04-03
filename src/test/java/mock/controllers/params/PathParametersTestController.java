package mock.controllers.params;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.params.PathParam;
import io.vertx.mvc.annotations.routing.Path;
import io.vertx.mvc.controllers.AbstractController;

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
