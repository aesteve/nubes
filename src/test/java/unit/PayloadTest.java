package unit;

import com.github.aesteve.vertx.nubes.marshallers.Payload;
import mock.domains.Dog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PayloadTest {
  @Test
  public void testPayloadType() {
    Payload<Dog> payload = new Payload<>();
    assertEquals(Void.class, payload.getType());
    payload.set(new Dog("Snoopy", "Beagle"));
    assertEquals(Dog.class, payload.getType());
  }
}
