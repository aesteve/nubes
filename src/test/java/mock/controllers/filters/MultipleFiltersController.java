package mock.controllers.filters;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

import io.vertx.ext.web.RoutingContext;

@Controller("/filters")
public class MultipleFiltersController {

    @BeforeFilter(2)
    public void before1(RoutingContext context) {
        context.response().write("before2;");
        context.next();
    }

    @BeforeFilter(3)
    public void before3(RoutingContext context) {
        context.response().write("before3;");
        context.next();
    }

    @BeforeFilter(1)
    public void before2(RoutingContext context) {
        context.response().setChunked(true);
        context.response().write("before1;");
        context.next();
    }

    @Path("/order")
    public void main(RoutingContext context) {
        context.next();
    }

    @AfterFilter(2)
    public void after2(RoutingContext context) {
        context.response().write("after2;");
        context.next();
    }

    @AfterFilter(3)
    public void after3(RoutingContext context) {
        context.response().write("after3;");
        context.response().end();
    }

    @AfterFilter(1)
    public void after1(RoutingContext context) {
        context.response().write("after1;");
        context.next();
    }

}