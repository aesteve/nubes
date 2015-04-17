package mock.controllers.api.throttling;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.mixins.ContentType;
import io.vertx.nubes.annotations.mixins.Throttled;
import io.vertx.nubes.annotations.routing.Path;
import io.vertx.nubes.marshallers.Payload;
import mock.domains.Dog;

@Controller("/throttling/")
@ContentType("application/json")
public class ThrottlingTestController {

    @Path("notThrottled")
    public void sendPublicDog(RoutingContext context, Payload<Dog> payload) {
        payload.set(new Dog("Pluto", "Mutt"));
        context.next();
    }

    @Path("dog")
    @Throttled
    public void sendDog(RoutingContext context, Payload<Dog> payload) {
        payload.set(new Dog("Idefix", "Westy"));
        context.next();
    }
}
