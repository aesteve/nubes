package mock.controllers;

import io.vertx.ext.web.RoutingContext;

import java.util.Locale;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

@Controller("/locales")
public class LocaleController {

    @Path("/echo")
    public void echoLocale(RoutingContext context, Locale locale) {
        context.response().end(locale.toString());
    }
}
