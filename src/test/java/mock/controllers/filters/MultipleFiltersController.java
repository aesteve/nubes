package mock.controllers.filters;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/filters")
public class MultipleFiltersController {

	@BeforeFilter(2)
	public void before1(HttpServerResponse response) {
		response.write("before2;");
	}

	@BeforeFilter(3)
	public void before3(HttpServerResponse response) {
		response.write("before3;");
	}

	@BeforeFilter(1)
	public void before2(HttpServerResponse response) {
		response.setChunked(true);
		response.write("before1;");
	}

	@GET("/order")
	public void main(RoutingContext context) {
		context.next();
	}

	@AfterFilter(2)
	public void after2(HttpServerResponse response) {
		response.write("after2;");
	}

	@AfterFilter(3)
	public void after3(HttpServerResponse response) {
		response.end("after3;");
	}

	@AfterFilter(1)
	public void after1(HttpServerResponse response) {
		response.write("after1;");
	}

}