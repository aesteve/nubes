package mock.controllers.cookies;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.cookies.Cookies;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

@Controller("/cookies/")
public class CookieTestController {

    @Path("noCookie")
    public void noCookie(RoutingContext context) {
        context.next();
    }

    @Path("setCookie")
    @Cookies
    public void setCookie(RoutingContext context) {
        context.addCookie(Cookie.cookie("dog", "Rantanplan"));
        context.next();
    }

    @Path("echo")
    @Cookies
    public void echoCookies(RoutingContext context) {
        String dog = context.getCookie("dog").getValue();
        context.response().end(dog);
    }

}
