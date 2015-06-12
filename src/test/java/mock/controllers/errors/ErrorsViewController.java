package mock.controllers.errors;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.exceptions.BadRequestException;

import io.vertx.ext.web.RoutingContext;

@Controller("/errors/view")
public class ErrorsViewController {

    @Path("/fail/:statusCode")
    @View("doesnotexistanyway")
    public void throwErrorUsingFail(RoutingContext context, Integer status) {
        switch (status) {
            case 400:
                context.fail(new BadRequestException("Deliberately failed with bad request"));
                break;
            case 500:
                context.fail(new RuntimeException("Deliberately failed with runtime exception"));
                break;

        }
    }

    @Path("/throw/:statusCode")
    @View("doesnotexistanyway")
    public void throwError(RoutingContext context, Integer status) throws Exception {
        switch (status) {
            case 400:
                throw new BadRequestException("Deliberately thrown bad request");
            case 500:
                throw new RuntimeException("Deliberately thrown runtime exception");
        }
    }
}
