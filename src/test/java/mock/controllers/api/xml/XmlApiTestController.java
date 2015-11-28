package mock.controllers.api.xml;

import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/xml/")
@ContentType("application/xml")
public class XmlApiTestController {

	@GET("noContent")
	public void noContent() {}

	@GET("dog")
	public void sendDomainObject(Payload<Dog> payload) {
		Dog snoopy = new Dog("Snoopy", "Beagle");
		payload.set(snoopy);
	}

	@POST("postdog")
	public void readDog(@RequestBody Dog dog, Payload<Dog> payload) {
		payload.set(dog); // echo back
	}
	
	@GET("exception")
	public void sendException() {
		throw new RuntimeException("Manually thrown exception");
	}

}
