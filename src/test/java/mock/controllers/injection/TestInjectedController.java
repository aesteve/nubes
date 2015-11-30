package mock.controllers.injection;

import integration.TestVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import mock.domains.Dog;
import mock.services.DogService;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.annotations.services.Service;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/inject")
@ContentType("application/json")
public class TestInjectedController {

	@Service(TestVerticle.DOG_SERVICE_NAME)
	private DogService dogService; // a service must be injected

	@Service(TestVerticle.SNOOPY_SERVICE_NAME)
	private Dog snoop; // a simple object registered as a service should be injected, too

	private Router router;

	@GET("/service")
	public void getDog(@Param("idx") Integer i, Payload<Dog> payload) {
		payload.set(dogService.getDog(i));
	}

	@GET("/class")
	public void getSimpleClass(Payload<Dog> payload) {
		payload.set(snoop);
	}

	@POST("/readBodyAsJsonObject")
	public JsonObject readBodyAsJsonObject(@RequestBody JsonObject json) {
		return json;
	}

	@POST("/readBodyAsJsonArray")
	public JsonArray readBodyAsJsonArray(@RequestBody JsonArray json) {
		return json;
	}

	@GET("/router")
	public JsonObject getRouter() {
		return new JsonObject().put("router", router.toString());
	}
}
