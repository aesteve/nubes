package mock.custom.controllers;

import java.util.Locale;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

import io.vertx.core.http.HttpServerResponse;

@Controller("/custom/locale")
public class LocaleController {

	@GET
	public void getLanguageTag(Locale loc, HttpServerResponse response) {
		response.end(loc.toLanguageTag());
	}
}
