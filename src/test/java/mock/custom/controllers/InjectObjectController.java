package mock.custom.controllers;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import mock.custom.annotations.InjectCustomObject;
import mock.custom.annotations.InjectCustomObjectByName;
import mock.custom.domains.CustomObject;

import java.util.Objects;

@Controller("/custom/params")
@ContentType("application/json")
public class InjectObjectController {
	
	@GET
	@InjectCustomObject
	public CustomObject resolveParam(CustomObject object) {
		Objects.requireNonNull(object);
		return object;
	}
	
	@GET("/byName")
	@InjectCustomObjectByName("other-name")
	public CustomObject resolveParamByName(CustomObject object) {
		Objects.requireNonNull(object);
		return object;
	}
}
