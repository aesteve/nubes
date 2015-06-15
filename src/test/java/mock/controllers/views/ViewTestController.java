package mock.controllers.views;

import io.vertx.ext.web.RoutingContext;
import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.context.ViewResolver;

@Controller("/views")
public class ViewTestController {

	@Path("/default")
	@View("index.html")
	public void rootView(RoutingContext context) {
		context.next();
	}

	@Path("/handlebars")
	@View("dog-name.hbs")
	public void handlebarsView(RoutingContext context, @Params Dog dog) {
		context.data().put("dog", dog);
		context.next();
	}

	@Path("/mvel")
	@View("dog-name.templ")
	public void mvelView(RoutingContext context, @Params Dog dog) {
		context.data().put("dog", dog);
		context.next();
	}

	@Path("/dynamic")
	@View
	public void dynamicViewName(RoutingContext context, @Params Dog dog) {
		ViewResolver.resolve(context, "dog-name.hbs");
		context.data().put("dog", dog);
		context.next();
	}
}
