package mock.controllers.cookies;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.Cookie;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Route;
import io.vertx.mvc.annotations.UsesCookies;
import io.vertx.mvc.controllers.AbstractController;

@Controller
@Route(path = "/cookies/")
public class CookieTestController extends AbstractController {

    @Route(path = "noCookie")
    public void noCookie(RoutingContext context) {
        context.next();
    }

    @Route(path = "setCookie")
    @UsesCookies
    public void setCookie(RoutingContext context) {
        context.addCookie(Cookie.cookie("dog", "Rantanplan"));
        context.next();
    }

    @Route(path = "echo")
    @UsesCookies
    public void echoCookies(RoutingContext context) {
        String dog = context.getCookie("dog").getValue();
        renderText(context, dog);
    }

}
