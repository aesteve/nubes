package mock.controllers.auth;

import io.vertx.ext.web.RoutingContext;
import mock.auth.MockUser;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.auth.User;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;

@Controller("/private")
public class PrivateController {

	@Path("/user")
	@Auth(authority = "", method = AuthMethod.BASIC)
	public void getUser(RoutingContext context, @User MockUser user) {
		context.response().end(user.login);
	}

	@Path("/admin")
	@Auth(authority = "admin", method = AuthMethod.BASIC)
	public void getAdmin(RoutingContext context, @User MockUser user) {
		context.response().end(user.login);
	}
}
