package mock.controllers.api.throttling;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.mixins.Throttled;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

import io.vertx.ext.web.RoutingContext;
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
