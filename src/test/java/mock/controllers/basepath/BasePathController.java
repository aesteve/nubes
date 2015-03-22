package mock.controllers.basepath;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Route;
import io.vertx.mvc.controllers.AbstractController;

@Route(path="/base/")
public class BasePathController extends AbstractController {

	@Route(path="test")
	public void shouldReflectCompletePath(RoutingContext context){
		
	}
}
