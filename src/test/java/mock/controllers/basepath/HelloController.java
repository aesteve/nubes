package mock.controllers.basepath;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

import io.vertx.ext.web.RoutingContext;

@Controller
public class HelloController {

    @Path("/hello")
    @GET
    public void sayHello(RoutingContext context) {
        context.response().end("Hello world!");
    }

}
