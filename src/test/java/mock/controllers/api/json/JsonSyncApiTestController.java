package mock.controllers.api.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;

@Controller("/json/sync/")
@ContentType("application/json")
public class JsonSyncApiTestController {

	@GET("noContent")
	public void noContent() {}

	@GET("jsonobject")
	public JsonObject sendJsonObject() {
		JsonObject json = new JsonObject();
		json.put("Bill", "Cocker");
		return json;
	}

	@GET("jsonarray")
	public JsonArray sendJsonArray() {
		JsonObject json = new JsonObject();
		json.put("Bill", "Cocker");
		List<JsonObject> list = new ArrayList<>(1);
		list.add(json);
		return new JsonArray(list);
	}

	@GET("map")
	public Map<String, String> sendMap() {
		Map<String, String> dogs = new HashMap<>(2);
		dogs.put("Snoopy", "Beagle");
		dogs.put("Bill", "Cocker");
		return dogs;
	}

	@GET("array")
	public List<String> sendArray() {
		List<String> dogs = new ArrayList<>(2);
		dogs.add("Snoopy");
		dogs.add("Bill");
		return dogs;
	}

	@GET("dog")
	public Dog sendDomainObject() {
		return new Dog("Snoopy", "Beagle");
	}

	@GET("dogs")
	public List<Dog> sendManyDomainObjects() {
		List<Dog> dogs = new ArrayList<>(2);
		Dog snoopy = new Dog("Snoopy", "Beagle");
		Dog bill = new Dog("Bill", "Cocker");
		dogs.add(snoopy);
		dogs.add(bill);
		return dogs;
	}

	@POST("postdog")
	public Dog postDog(@RequestBody Dog dog) {
		return dog;
	}

	@GET("nothing")
	public Dog returnNothing() {
		return null;
	}
}
