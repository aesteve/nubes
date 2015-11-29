package mock.custom.controllers;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/custom/errorHandler")
@ContentType("text/plain")
public class ErrorHandlerTestController {
	
	public static String EXCEPTION_MSG = "Something wrong happened";
	
	@GET
	public void error() {
		throw new RuntimeException(EXCEPTION_MSG);
	}
}
