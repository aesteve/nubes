package mock.controllers.params;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.params.Params;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import io.vertx.ext.web.RoutingContext;
import mock.domains.Dog;

@Controller("/params/form/")
public class FormParametersTestController {

	public enum Animal {
		CAT, DOG, LIZARD
	}

	@GET("string")
	@POST("string")
	public void testParam(RoutingContext context, @Param("parameter") String parameter) {
		context.response().end(parameter);
	}

	@GET("dog")
	@POST("dog")
	public void testParam(RoutingContext context, @Params Dog dog) {
		context.response().end(dog.toString());
	}

	@GET("boolean")
	@POST("boolean")
	public void testParam(RoutingContext context, @Param Boolean value) {
		context.response().end(Boolean.toString(value));
	}
}
