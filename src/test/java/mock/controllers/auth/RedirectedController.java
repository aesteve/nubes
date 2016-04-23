package mock.controllers.auth;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.core.http.HttpServerResponse;

import static com.github.aesteve.vertx.nubes.auth.AuthMethod.REDIRECT;

@Controller("/auth/redirected")
public class RedirectedController {

	public final static String REDIRECT_URL = "/assets/login-page.html";

	@GET("/private")
	@Auth(method = REDIRECT, redirectURL = REDIRECT_URL, authority = "")
	public void privatePath(HttpServerResponse response) {
		response.end("Secret page !");
	}

}
