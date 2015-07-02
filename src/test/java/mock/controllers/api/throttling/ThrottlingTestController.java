package mock.controllers.api.throttling;

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
	public void sendPublicDog(Payload<Dog> payload) {
		payload.set(new Dog("Pluto", "Mutt"));
	}

	@GET("dog")
	@Throttled
	public void sendDog(Payload<Dog> payload) {
		payload.set(new Dog("Idefix", "Westy"));
	}
}
