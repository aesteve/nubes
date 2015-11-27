package unit;

import static org.junit.Assert.*;
import mock.domains.Dog;

import org.junit.Test;

import com.github.aesteve.vertx.nubes.marshallers.Payload;

public class PayloadTest {
	@Test
	public void testPayloadType() {
		Payload<Dog> payload = new Payload<>();
		assertEquals(Void.class, payload.getType());
		payload.set(new Dog("Snoopy", "Beagle"));
		assertEquals(Dog.class, payload.getType());
	}
}
