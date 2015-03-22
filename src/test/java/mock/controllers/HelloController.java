package mock.controllers;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Route;
import io.vertx.mvc.controllers.AbstractController;

public class HelloController extends AbstractController {
	
	@Route(path="/hello", method=HttpMethod.GET)
	public void sayHello(RoutingContext context){
		render(context, "Hello world!");
	}
	
}
