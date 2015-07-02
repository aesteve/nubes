package mock.controllers.api.pagination;

import io.vertx.core.http.HttpServerResponse;

import java.util.ArrayList;
import java.util.List;

import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.context.PaginationContext;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/pagination/")
@ContentType("application/json")
public class PaginationTestController {

	/**
	 * Not paginated. Doesn't matter we use pagination on other methods
	 */
	@GET("notPaginated")
	public void notPaginated(HttpServerResponse response) {
		response.end();
	}

	/**
	 * Paginated, with one single result
	 * A warning should be logged but should not fail
	 */
	@GET("paginationContextAvailable")
	public void paginationContextAvailable(PaginationContext pageContext, Payload<Dog> payload) {
		payload.set(new Dog("Milou", "Fox terrier"));
	}

	/**
	 * Paginated, sends as many results as specified in "nbResults" query parameter
	 */
	@GET("sendResults")
	public void sendResults(@Param("nbResults") Integer nbResults, PaginationContext pageContext, Payload<List<Dog>> payload) {
		List<Dog> dogs = new ArrayList<>(nbResults);
		for (int i = 0; i < nbResults; i++) {
			dogs.add(new Dog("My name is dog number " + i + " I wish I have a real name :'( ", "Border collie"));
		}
		// User will have to truncate it's data (especially set a Limit on the database query for example)
		pageContext.setNbItems(nbResults);
		payload.set(dogs.subList(pageContext.firstItemInPage(), pageContext.lastItemInPage()));
	}
}
