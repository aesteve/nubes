package mock.controllers.auth;

import io.vertx.ext.web.RoutingContext;
import mock.auth.MockUser;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.auth.Logout;
import com.github.aesteve.vertx.nubes.annotations.auth.User;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/private")
public class PrivateController {

	@GET("/user")
	@Auth(authority = "", method = AuthMethod.BASIC)
	public void getUser(RoutingContext context, @User MockUser user) {
		context.response().end(user.login);
	}

	@GET("/admin")
	@Auth(authority = "admin", method = AuthMethod.BASIC)
	public void getAdmin(RoutingContext context, @User MockUser user) {
		context.response().end(user.login);
	}

	@GET("/api")
	@Auth(authority = "admin", method = AuthMethod.API_TOKEN)
	public String getApi(@User MockUser user) {
		return user.login;
	}

	@GET("/logout")
	@Auth(authority = "admin", method = AuthMethod.API_TOKEN)
	@Logout
	public void logout(Payload<Void> payload) {
		payload.set(null);
	}

}
