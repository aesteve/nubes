package mock.controllers.api.json;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.mixins.ContentType;
import io.vertx.mvc.annotations.params.RequestBody;
import io.vertx.mvc.annotations.routing.POST;
import io.vertx.mvc.annotations.routing.Path;
import io.vertx.mvc.marshallers.Payload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mock.domains.Dog;

@Controller("/json/")
@ContentType("application/json")
public class JsonApiTestController {

    private final Dog snoopy = new Dog("Snoopy", "Beagle");
    private final Dog bill = new Dog("Bill", "Cocker");

    @Path("noContent")
    public void noContext(RoutingContext context) {
        context.next();
    }

    @Path("map")
    public void sendMap(RoutingContext context, Payload<Map<String, String>> payload) {
        Map<String, String> dogs = new HashMap<String, String>();
        dogs.put("Snoopy", "Beagle");
        dogs.put("Bill", "Cocker");
        payload.set(dogs);
        context.next();
    }

    @Path("array")
    public void sendArray(RoutingContext context, Payload<List<String>> payload) {
        List<String> dogs = new ArrayList<String>(2);
        dogs.add("Snoopy");
        dogs.add("Bill");
        payload.set(dogs);
        context.next();
    }

    @Path("dog")
    public void sendDomainObject(RoutingContext context, Payload<Dog> payload) {
        payload.set(snoopy);
        context.next();
    }

    @Path("dogs")
    public void sendManyDomainObjects(RoutingContext context, Payload<List<Dog>> payload) {
        List<Dog> dogs = new ArrayList<Dog>(2);
        dogs.add(snoopy);
        dogs.add(bill);
        payload.set(dogs);
        context.next();
    }
    
    @Path("postdog")
    @POST
    public void readDog(@RequestBody Dog dog, RoutingContext context, Payload<Dog> payload) {
    	payload.set(dog); // echo back
    	context.next();
    	
    }

}
