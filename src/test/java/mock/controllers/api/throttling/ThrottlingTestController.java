package mock.controllers.api.throttling;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Path;
import io.vertx.mvc.annotations.Throttled;
import io.vertx.mvc.controllers.impl.JsonApiController;
import mock.domains.Dog;

@Controller("/throttling/")
public class ThrottlingTestController extends JsonApiController {

    @Path("notThrottled")
    public void sendPublicDog(RoutingContext context) {
        setPayload(context, new Dog("Pluto", "Mutt"));
        context.next();
    }

    @Path("dog")
    @Throttled
    public void sendDog(RoutingContext context) {
        setPayload(context, new Dog("Idefix", "Westy"));
        context.next();
    }
}
