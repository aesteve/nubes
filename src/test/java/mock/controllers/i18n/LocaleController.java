package mock.controllers.i18n;

import io.vertx.ext.web.RoutingContext;

import java.util.Locale;
import java.util.ResourceBundle;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

@Controller("/locales")
public class LocaleController {

    @Path("/echo")
    public void echoLocale(RoutingContext context, Locale locale) {
        context.response().end(locale.toString());
    }

    @Path("/greet")
    public void greetMe(RoutingContext context, ResourceBundle i18n) {
        context.response().end(i18n.getString("greetings"));
    }
}
