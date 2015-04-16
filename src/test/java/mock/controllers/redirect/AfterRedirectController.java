package mock.controllers.redirect;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.filters.AfterFilter;
import io.vertx.mvc.annotations.filters.BeforeFilter;
import io.vertx.mvc.annotations.routing.Path;

@Controller("/accessibleAfterRedirect")
public class AfterRedirectController {
	
	@BeforeFilter
	public void before(RoutingContext context) {
		context.response().putHeader("afterredirect-beforefilter", "true");
		context.next();
	}
	
	@Path("/after")
	public void handleRedirect(RoutingContext context) {
		context.response().putHeader("afterredirect-method", "true");
		context.next();
	}
	
	@AfterFilter
	public void after(RoutingContext context) {
		context.response().putHeader("after-redirect-afterfilter", "true");
		context.response().end();
	}
}
