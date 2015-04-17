package mock.controllers.redirect;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.filters.AfterFilter;
import io.vertx.nubes.annotations.filters.BeforeFilter;
import io.vertx.nubes.annotations.routing.Path;

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
        context.response().putHeader("afterredirect-afterfilter", "true");
        context.response().setStatusCode(204);
        context.response().end();
    }
}
