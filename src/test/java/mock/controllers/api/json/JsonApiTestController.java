package mock.controllers.api.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mock.domains.Dog;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.Controller;
import io.vertx.mvc.annotations.Route;
import io.vertx.mvc.controllers.impl.JsonApiController;

@Controller
@Route(path="/json/")
public class JsonApiTestController extends JsonApiController {
	
	private final Dog snoopy = new Dog("Snoopy", "Beagle");
	private final Dog bill = new Dog("Bill", "Cocker");
	
	@Route(path="noContent")
	public void noContext(RoutingContext context) {
		context.next();
	}
	
	@Route(path="map")
	public void sendMap(RoutingContext context){
		Map<String, String> dogs = new HashMap<String, String>();
		dogs.put("Snoopy", "Beagle");
		dogs.put("Bill", "Cocker");
		context.data().put("payload", dogs);
		context.next();
	}
	
	@Route(path="array")
	public void sendArray(RoutingContext context){
		List<String> dogs = new ArrayList<String>(2);
		dogs.add("Snoopy");
		dogs.add("Bill");
		context.data().put("payload", dogs);
		context.next();
	}
	
	@Route(path="dog")
	public void sendDomainObject(RoutingContext context){
		context.data().put("payload", snoopy);
		context.next();
	}
	
	@Route(path="dogs")
	public void sendManyDomainObjects(RoutingContext context){
		List<Dog> dogs = new ArrayList<Dog>(2);
		dogs.add(snoopy);
		dogs.add(bill);
		context.data().put("payload", dogs);
		context.next();
	}
	
}
