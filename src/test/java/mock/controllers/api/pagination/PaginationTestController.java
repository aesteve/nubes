package mock.controllers.api.pagination;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.mixins.ContentType;
import io.vertx.mvc.annotations.routing.Path;
import io.vertx.mvc.context.PaginationContext;
import io.vertx.mvc.marshallers.Payload;

import java.util.ArrayList;
import java.util.List;

import mock.domains.Dog;

@Controller("/pagination/")
@ContentType("application/json")
public class PaginationTestController {

    /**
     * Not paginated. Doesn't matter we set @Paginated on other methods
     * 
     * @param context
     */
    @Path("notPaginated")
    public void notPaginated(RoutingContext context) {
        context.next();
    }

    /**
     * Not annotated as paginated but trying to use PaginationContext
     * Should throw an exception and context should fail properly
     * 
     * @param context
     */
    @Path("notPaginatedButUsingPagination")
    public void notPaginatedButUsingPagination(RoutingContext context, PaginationContext pageContext) {
        context.next();
    }

    /**
     * Paginated, with one single result
     * A warning should be logged but should not fail
     * 
     * @param context
     */
    @Path("paginationContextAvailable")
    public void paginationContextAvailable(RoutingContext context, PaginationContext pageContext, Payload<Dog> payload) {
        payload.set(new Dog("Milou", "Fox terrier"));
        context.next();
    }

    /**
     * Paginated, sends as many results as specified in "nbResults" query parameter
     * 
     */
    @Path("sendResults")
    public void sendResults(RoutingContext context, PaginationContext pageContext, Payload<List<Dog>> payload) {
        Integer nbResults = Integer.valueOf(context.request().getParam("nbResults"));
        List<Dog> dogs = new ArrayList<Dog>(nbResults);
        for (int i = 0; i < nbResults; i++) {
            dogs.add(new Dog("My name is dog number " + i + " I wish I have a real name :'( ", "Border collie"));
        }
        // User will have to truncate it's data (especially set a Limit on the database query for example)
        pageContext.setNbItems(nbResults);
        payload.set(dogs.subList(pageContext.firstItemInPage(), pageContext.lastItemInPage()));
        context.next();
    }
}
