package mock.controllers.cookies;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.annotations.cookies.Cookies;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

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

	@GET("echoByName")
	@Cookies
	public void echoCookieByName(HttpServerResponse response, @CookieValue String dog) {
		response.end(dog);
	}

	@GET("echoObject")
	@Cookies
	public void echoCookieObject(HttpServerResponse response, @CookieValue Cookie dog) {
		response.end(dog.getValue());
	}

}
