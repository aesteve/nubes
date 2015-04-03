package mock.controllers.filters;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.filters.AfterFilter;
import io.vertx.mvc.annotations.filters.BeforeFilter;
import io.vertx.mvc.annotations.filters.Finalizer;
import io.vertx.mvc.annotations.routing.Path;
import io.vertx.mvc.controllers.AbstractController;

@Controller
public class FilteredController extends AbstractController {

    @BeforeFilter
    public void beforeFilter1(RoutingContext context) {
        context.data().put("BeforeFilter1", true);
        context.next();
    }

    @BeforeFilter
    public void beforeFilter2(RoutingContext context) {
        context.data().put("BeforeFilter2", true);
        context.next();
    }

    @Path("/filtered1")
    public void mainHandler1(RoutingContext context) {
        context.data().put("Filtered1", true);
        context.next();
    }

    @Path("/filtered2")
    public void mainHandler2(RoutingContext context) {
        context.data().put("Filtered2", true);
        context.next();
    }

    @AfterFilter
    public void afterFilter1(RoutingContext context) {
        context.data().put("AfterFilter1", true);
        context.next();
    }

    @AfterFilter
    public void afterFilter2(RoutingContext context) {
        context.data().put("AfterFilter2", true);
        context.next();
    }

    @Finalizer
    public void writeData(RoutingContext context) {
        JsonObject obj = new JsonObject(context.data());
        renderText(context, obj.toString());
    }

}
