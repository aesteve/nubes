package mock.controllers.errors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.nubes.annotations.Controller;
import io.vertx.nubes.annotations.View;
import io.vertx.nubes.annotations.routing.Path;
import io.vertx.nubes.exceptions.BadRequestException;

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
