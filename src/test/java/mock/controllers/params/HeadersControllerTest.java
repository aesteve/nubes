package mock.controllers.params;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.Header;
import com.github.aesteve.vertx.nubes.annotations.params.Headers;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/headers/")
public class HeadersControllerTest {

	@GET("mandatory")
	public void mandatoryHeader(RoutingContext context, @Header(value = "X-Date", mandatory = true) Date date) {
		context.response().end(Long.toString(date.getTime()));
	}

	@GET("facultative")
	public void facultativeHeader(RoutingContext context, @Header("X-Date") Date date) {
		if (date == null) { // allowed
			context.response().end("null");
		} else {
			context.response().end(Long.toString(date.getTime()));
		}
	}

	@GET("echoByName")
	public void getHeaderByName(HttpServerResponse response, @Header String someHeader) {
		response.end(someHeader);
	}

	@GET("wrongHeaders")
	public void getHeaders(HttpServerResponse response, @Headers String headers) {
		response.end(headers);
	}
}
