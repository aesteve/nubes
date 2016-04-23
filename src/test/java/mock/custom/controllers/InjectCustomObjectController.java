package mock.custom.controllers;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import mock.custom.domains.CustomObject;

@Controller("/custom/params/adapter")
@ContentType("application/json")
public class InjectCustomObjectController {
		
	@GET
	public CustomObject echoCustomObject(@Param CustomObject custom) {
		return custom;
	}
}
