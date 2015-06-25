package mock.controllers.views;

import io.vertx.ext.web.RoutingContext;
import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.context.ViewResolver;

@Controller("/views")
public class ViewTestController {

	@GET("/default")
	@View("index.html")
	public void rootView(RoutingContext context) {
		context.next();
	}

	@GET("/handlebars")
	@View("dog-name.hbs")
	public void handlebarsView(RoutingContext context, @Params Dog dog) {
		context.data().put("dog", dog);
		context.next();
	}

	@GET("/mvel")
	@View("dog-name.templ")
	public void mvelView(RoutingContext context, @Params Dog dog) {
		context.data().put("dog", dog);
		context.next();
	}

	@GET("/dynamic")
	@View
	public void dynamicViewName(RoutingContext context, @Params Dog dog) {
		ViewResolver.resolve(context, "dog-name.hbs");
		context.data().put("dog", dog);
		context.next();
	}
}
