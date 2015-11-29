package mock.custom.controllers;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

import io.vertx.core.http.HttpServerResponse;
import mock.custom.annotations.SimpleAnnot;

@Controller("/custom/params/annotated")
public class AnnotatedParamController {
	
	@GET
	public void getSimpleAnnot(@SimpleAnnot("something") String something, HttpServerResponse response) {
		response.end(something);
	}
	
}
