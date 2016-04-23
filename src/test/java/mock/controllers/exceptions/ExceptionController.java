package mock.controllers.exceptions;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.exceptions.ValidationException;
import com.github.aesteve.vertx.nubes.exceptions.http.impl.BadRequestException;
import com.github.aesteve.vertx.nubes.exceptions.http.impl.ForbiddenException;
import com.github.aesteve.vertx.nubes.exceptions.http.impl.NotFoundException;
import com.github.aesteve.vertx.nubes.exceptions.http.impl.UnauthorizedException;

import java.util.Arrays;

@Controller("/exceptions")
@ContentType("application/json")
public class ExceptionController {

	@GET("/badrequest")
	public void badRequest() throws BadRequestException {
		throw new BadRequestException();
	}

	@GET("/badrequest2")
	public void badRequest2() throws BadRequestException {
		throw new BadRequestException("I can't understand");
	}
	
	@GET("/forbidden")
	public void forbidden() throws ForbiddenException {
		throw new ForbiddenException();
	}

	@GET("/forbidden2")
	public void forbidden2() throws ForbiddenException {
		throw new ForbiddenException("Sorry :(");
	}
	
	@GET("/notfound")
	public void notFound() throws NotFoundException {
		throw new NotFoundException();
	}

	@GET("/notfound2")
	public void notFound2() throws NotFoundException {
		throw new NotFoundException("Some resource");
	}
	
	@GET("/unauthorized")
	public void unauthorized() throws UnauthorizedException {
		throw new UnauthorizedException();
	}
	
	@GET("/validation")
	public void validated() throws BadRequestException {
		ValidationException ve = new ValidationException(Arrays.asList("field1 is missing", "field2 is missing"));
		throw new BadRequestException(ve);
	}
}
