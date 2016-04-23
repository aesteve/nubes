package mock.controllers.injection;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.annotations.services.ServiceProxy;
import io.vertx.core.http.HttpServerResponse;
import mock.services.ParrotService;

@Controller("/injectedProxy")
public class TestInjectedProxyController {

	@ServiceProxy("service.parrot")
	ParrotService parrot;

	@POST
	public void echoThroughParrot(HttpServerResponse response, @RequestBody String body) {
		parrot.echo(body, reply -> {
			response.end(reply.result());
		});
	}
}
