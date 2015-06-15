package mock.controllers.filters;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.filters.After;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.Before;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

@Controller("/filters")
public class AOPFilter {

    public static final long EXECUTION_TIME = 100; // ms

    @Path("/aop")
    @Before(name = "setDateBefore")
    @After(name = "setDateAfter")
    public void beforeAndAfter(RoutingContext context) {
        try {
            Thread.sleep(EXECUTION_TIME); // just to make sure that 'After' is slightly different from 'Before'
        } catch (Exception e) {

        }
        context.next();
    }

    @AfterFilter
    public void endResponse(RoutingContext context) {
        context.response().end();
    }
}
