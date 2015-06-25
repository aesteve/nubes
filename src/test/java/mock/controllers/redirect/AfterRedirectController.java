package mock.controllers.redirect;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/accessibleAfterRedirect")
public class AfterRedirectController {

	@BeforeFilter
	public void before(RoutingContext context) {
		context.response().putHeader("afterredirect-beforefilter", "true");
		context.next();
	}

	@GET("/after")
	public void handleRedirect(RoutingContext context) {
		context.response().putHeader("afterredirect-method", "true");
		context.next();
	}

	@AfterFilter
	public void after(RoutingContext context) {
		context.response().putHeader("afterredirect-afterfilter", "true");
		context.response().setStatusCode(204);
		context.response().end();
	}
}
