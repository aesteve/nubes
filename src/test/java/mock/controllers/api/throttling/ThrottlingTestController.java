package mock.controllers.api.throttling;

import io.vertx.ext.web.RoutingContext;
import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.mixins.Throttled;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/throttling/")
@ContentType("application/json")
public class ThrottlingTestController {

	@GET("notThrottled")
	public void sendPublicDog(RoutingContext context, Payload<Dog> payload) {
		payload.set(new Dog("Pluto", "Mutt"));
		context.next();
	}

	@GET("dog")
	@Throttled
	public void sendDog(RoutingContext context, Payload<Dog> payload) {
		payload.set(new Dog("Idefix", "Westy"));
		context.next();
	}
}
