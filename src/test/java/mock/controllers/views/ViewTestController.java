package mock.controllers.views;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

import io.vertx.ext.web.RoutingContext;
import mock.domains.Dog;

@Controller("/views")
public class ViewTestController {

    @Path("/default")
    @View("index.html")
    public void rootView(RoutingContext context) {
        context.next();
    }

    @Path("/handlebars")
    @View("dog-name.hbs")
    public void handlebarsView(RoutingContext context, @Params Dog dog) {
        context.data().put("dog", dog);
        context.next();
    }

    @Path("/mvel")
    @View("dog-name.templ")
    public void mvelView(RoutingContext context, @Params Dog dog) {
        context.data().put("dog", dog);
        context.next();
    }
}
