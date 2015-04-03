package mock.controllers.params;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.params.Param;
import io.vertx.mvc.annotations.routing.Path;
import io.vertx.mvc.controllers.AbstractController;

import java.util.Date;

@Controller("/params/query/")
public class QueryParametersTestController extends AbstractController {
	
	public enum Animal {
		CAT, DOG, LIZARD;
	}

    @Path("string")
    public void testParam(RoutingContext context, @Param(value="parameter", mandatory=true) String parameter) {
        renderText(context, parameter);
    }
    
    @Path("date")
    public void testParam(RoutingContext context, @Param("parameter") Date parameter) {
        renderText(context, Long.toString(parameter.getTime()));
    }
    
    @Path("enum")
    public void testParam(RoutingContext context, @Param("parameter") Animal parameter) {
        renderText(context, parameter.toString());
    }
    
    @Path("long")
    public void testParam(RoutingContext context, @Param("parameter") Long parameter) {
        renderText(context, parameter.toString());
    }
    
    @Path("int")
    public void testParam(RoutingContext context, @Param("parameter") Integer parameter) {
        renderText(context, parameter.toString());
    }
    
    @Path("float")
    public void testParam(RoutingContext context, @Param("parameter") Float parameter) {
        renderText(context, parameter.toString());
    }
}
