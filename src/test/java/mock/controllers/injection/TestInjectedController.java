package mock.controllers.injection;

import integration.TestVerticle;
import mock.domains.Dog;
import mock.services.DogService;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.services.Service;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/inject")
@ContentType("application/json")
public class TestInjectedController {

	@Service(TestVerticle.DOG_SERVICE_NAME)
	private DogService dogService; // a service must be injected

	@Service(TestVerticle.SNOOPY_SERVICE_NAME)
	private Dog snoop; // a simple object registered as a service should be injected, too

	@GET("/service")
	public void getDog(@Param("idx") Integer i, Payload<Dog> payload) {
		payload.set(dogService.getDog(i));
	}

	@GET("/class")
	public void getSimpleClass(Payload<Dog> payload) {
		payload.set(snoop);
	}
}
