package mock.controllers.api.json;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/json/")
@ContentType("application/json")
public class JsonApiTestController {

	@GET("noContent")
	public void noContent(RoutingContext context) {
		context.next();
	}

	@GET("jsonobject")
	public void sendJsonObject(RoutingContext context, Payload<JsonObject> payload) {
		JsonObject json = new JsonObject();
		json.put("Bill", "Cocker");
		payload.set(json);
		context.next();
	}

	@GET("map")
	public void sendMap(RoutingContext context, Payload<Map<String, String>> payload) {
		Map<String, String> dogs = new HashMap<>(2);
		dogs.put("Snoopy", "Beagle");
		dogs.put("Bill", "Cocker");
		payload.set(dogs);
		context.next();
	}

	@GET("array")
	public void sendArray(RoutingContext context, Payload<List<String>> payload) {
		List<String> dogs = new ArrayList<>(2);
		dogs.add("Snoopy");
		dogs.add("Bill");
		payload.set(dogs);
		context.next();
	}

	@GET("dog")
	public void sendDomainObject(RoutingContext context, Payload<Dog> payload) {
		Dog snoopy = new Dog("Snoopy", "Beagle");
		payload.set(snoopy);
		context.next();
	}

	@GET("dogs")
	public void sendManyDomainObjects(RoutingContext context, Payload<List<Dog>> payload) {
		List<Dog> dogs = new ArrayList<>(2);
		Dog snoopy = new Dog("Snoopy", "Beagle");
		Dog bill = new Dog("Bill", "Cocker");
		dogs.add(snoopy);
		dogs.add(bill);
		payload.set(dogs);
		context.next();
	}

	@POST("postdog")
	public void postDog(@RequestBody Dog dog, RoutingContext context, Payload<Dog> payload) {
		payload.set(dog); // echo back
		context.next();
	}

	@GET("fail/:statusCode")
	public void sendStatusCode(RoutingContext context, @Param("statusCode") Integer statusCode) {
		context.fail(statusCode);
	}

}
