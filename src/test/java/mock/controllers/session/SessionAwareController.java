package mock.controllers.session;


import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.ext.web.Session;

@Controller("/api/session")
public class SessionAwareController {

	@GET
	public String getSession(Session session) {
		return session.toString(); // should not be null
	}

}
