package mock.controllers.redirect;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.core.http.HttpServerResponse;

@Controller("/accessibleAfterRedirect")
public class AfterRedirectController {

	@BeforeFilter
	public void before(HttpServerResponse response) {
		response.putHeader("afterredirect-beforefilter", "true");
	}

	@GET("/after")
	public void handleRedirect(HttpServerResponse response) {
		response.putHeader("afterredirect-method", "true");
	}

	@AfterFilter
	public void after(HttpServerResponse response) {
		response.putHeader("afterredirect-afterfilter", "true");
		response.setStatusCode(204);
		response.end();
	}
}
