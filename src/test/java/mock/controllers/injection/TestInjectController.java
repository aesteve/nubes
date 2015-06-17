package mock.controllers.injection;

import mock.domains.Dog;
import mock.services.DogService;
import integration.TestVerticle;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.annotations.services.Service;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/inject")
@ContentType("application/json")
public class TestInjectController {

    @Service(TestVerticle.DOG_SERVICE_NAME)
    private DogService dogService; // a service must be injected

    @Service(TestVerticle.SNOOPY_SERVICE_NAME)
    private Dog snoop; // a simple object registered as a service should be injected, too

    @Path("/service")
    public void getDog(RoutingContext context, @Param("idx") Integer i, Payload<Dog> payload) {
        payload.set(dogService.getDog(i));
        context.next();
    }

    @Path("/class")
    public void getSimpleClass(RoutingContext context, Payload<Dog> payload) {
        payload.set(snoop);
        context.next();
    }
}
