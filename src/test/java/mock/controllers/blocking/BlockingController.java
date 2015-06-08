package mock.controllers.blocking;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.Blocking;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.routing.Path;

@Controller("/blocking")
public class BlockingController {
    @Path("/test")
    @Blocking
    public void test(RoutingContext context) throws Exception {
        Thread.sleep(3000);
        context.response().end("Done.");
    }
}
