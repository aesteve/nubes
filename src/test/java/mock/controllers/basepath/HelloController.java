package mock.controllers.basepath;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller
public class HelloController {

    @GET("/hello")
    public void sayHello(RoutingContext context) {
        context.response().end("Hello world!");
    }

}
