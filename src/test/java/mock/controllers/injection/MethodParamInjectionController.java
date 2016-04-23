package mock.controllers.injection;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.Headers;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;

@Controller("/params/injection")
public class MethodParamInjectionController {

	@GET("/socketAddress")
	@ContentType("application/json")
	public JsonObject getSocketAddress(SocketAddress address) {
		return toJson(address);
	}

	@GET("/httpVersion")
	@ContentType("application/json")
	public JsonObject getNetSocket(HttpVersion version) {
		return new JsonObject()
				.put("version", version.toString());
	}

	@GET("/headers")
	@ContentType("text/plain")
	public String getHeader(@Headers MultiMap headers, @Param String headerName) {
		return headers.get(headerName);
	}

	@GET("/request")
	@ContentType("text/plain")
	public String getRequest(HttpServerRequest request) {
		return request.uri();
	}

	@GET("/eventBus")
	@ContentType("text/plain")
	public String getEventBus(EventBus eb) {
		return eb.toString();
	}

	private static JsonObject toJson(SocketAddress address) {
		return new JsonObject()
				.put("host", address.host())
				.put("port", address.port());
	}

}
