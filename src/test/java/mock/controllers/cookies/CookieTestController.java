package mock.controllers.cookies;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.cookies.Cookies;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/cookies/")
public class CookieTestController {

    @GET("noCookie")
    public void noCookie(RoutingContext context) {
        context.next();
    }

    @GET("setCookie")
    @Cookies
    public void setCookie(RoutingContext context) {
        context.addCookie(Cookie.cookie("dog", "Rantanplan"));
        context.next();
    }

    @GET("echo")
    @Cookies
    public void echoCookies(RoutingContext context) {
        String dog = context.getCookie("dog").getValue();
        context.response().end(dog);
    }

}
