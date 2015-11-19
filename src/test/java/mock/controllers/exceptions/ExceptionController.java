package mock.controllers.exceptions;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.exceptions.http.impl.BadRequestException;
import com.github.aesteve.vertx.nubes.exceptions.http.impl.ForbiddenException;
import com.github.aesteve.vertx.nubes.exceptions.http.impl.NotFoundException;
import com.github.aesteve.vertx.nubes.exceptions.http.impl.UnauthorizedException;

@Controller("/exceptions")
@ContentType("application/json")
public class ExceptionController {

	@GET("/badrequest")
	public void badRequest() throws BadRequestException {
		throw new BadRequestException();
	}

	@GET("/forbidden")
	public void forbidden() throws ForbiddenException {
		throw new ForbiddenException();
	}

	@GET("/notfound")
	public void notFound() throws NotFoundException {
		throw new NotFoundException();
	}

	@GET("/unauthorized")
	public void unauthorized() throws UnauthorizedException {
		throw new UnauthorizedException();
	}
}
