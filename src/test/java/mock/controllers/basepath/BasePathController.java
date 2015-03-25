package mock.controllers.basepath;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Route;
import io.vertx.mvc.controllers.AbstractController;

@Controller
@Route(path="/base/")
public class BasePathController extends AbstractController {

	@Route(path="test")
	public void shouldReflectCompletePath(RoutingContext context){
		renderText(context, "/base/test");
	}
}
