package mock.controllers.api.xml;

import io.vertx.ext.web.RoutingContext;
import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

@Controller("/xml/")
@ContentType("application/xml")
public class XmlApiTestController {

    @GET("noContent")
    public void noContent(RoutingContext context) {
        context.next();
    }

    @GET("dog")
    public void sendDomainObject(RoutingContext context, Payload<Dog> payload) {
        Dog snoopy = new Dog("Snoopy", "Beagle");
        payload.set(snoopy);
        context.next();
    }

    @POST("postdog")
    public void readDog(@RequestBody Dog dog, RoutingContext context, Payload<Dog> payload) {
        payload.set(dog); // echo back
        context.next();
    }

}
