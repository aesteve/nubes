package mock.controllers.shared;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.params.LocalMapValue;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.params.VertxLocalMap;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.RoutingContext;

@Controller("/shared/local")
public class TestLocalMap {

	@GET("/staticValue")
	public void getLocalValue(RoutingContext context, @LocalMapValue(mapName = "test-map", key = "key") String value) {
		context.response().putHeader("X-Map-Value", value);
		context.response().end();
	}

	@GET("/staticValueWithParam")
	public void getLocalValueWithParamName(RoutingContext context, @LocalMapValue(mapName = "test-map") String key) {
		context.response().putHeader("X-Map-Value", key);
		context.response().end();
	}

	@GET("/dynamicValue")
	public void getDynamicValue(RoutingContext context, @VertxLocalMap("test-map") LocalMap<String, String> map, @Param("key") String key) {
		context.response().putHeader("X-Map-Value", map.get(key));
		context.response().end();
	}

	@GET("/dynamicValueWithParamName")
	public void getDynamicValueWithParamName(HttpServerResponse response, @VertxLocalMap LocalMap<String, String> someMap, @Param String key) {
		response.putHeader("X-Map-Value", someMap.get(key));
		response.end();
	}
}
