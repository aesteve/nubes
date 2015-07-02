package mock.controllers.cookies;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.annotations.cookies.Cookies;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;

@Controller("/cookies/")
public class CookieTestController {

	@GET("noCookie")
	public void noCookie(HttpServerResponse response) {
		response.end();
	}

	@GET("setCookie")
	@Cookies
	public void setCookie(RoutingContext context) {
		context.addCookie(Cookie.cookie("dog", "Rantanplan"));
		context.response().end();
	}

	@GET("echo")
	@Cookies
	public void echoCookies(HttpServerResponse response, @CookieValue("dog") String dog) {
		response.end(dog);
	}

}
