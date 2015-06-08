package mock.controllers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.routing.Path;

import java.util.Locale;

@Controller("/locales")
public class LocaleController {

    @Path("/echo")
    public void echoLocale(RoutingContext context, Locale locale) {
        context.response().end(locale.toString());
    }
}
