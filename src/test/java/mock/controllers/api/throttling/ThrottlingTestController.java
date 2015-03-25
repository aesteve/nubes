package mock.controllers.api.throttling;

import mock.domains.Dog;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Route;
import io.vertx.mvc.annotations.Throttled;
import io.vertx.mvc.controllers.impl.JsonApiController;

@Controller
@Route(path = "/throttling/")
public class ThrottlingTestController extends JsonApiController {

    @Route(path = "notThrottled")
    public void sendPublicDog(RoutingContext context) {
        setPayload(context, new Dog("Pluto", "Mutt"));
        context.next();
    }

    @Route(path = "dog")
    @Throttled
    public void sendDog(RoutingContext context) {
        setPayload(context, new Dog("Idefix", "Westy"));
        context.next();
    }
}
