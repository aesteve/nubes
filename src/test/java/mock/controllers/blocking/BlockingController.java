package mock.controllers.blocking;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Blocking;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/blocking")
public class BlockingController {
    @GET("/test")
    @Blocking
    public void test(RoutingContext context) throws Exception {
        Thread.sleep(3000);
        context.response().end("Done.");
    }
}
