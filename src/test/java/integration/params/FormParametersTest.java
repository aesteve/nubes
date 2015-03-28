package integration.params;

import static org.junit.Assert.assertEquals;

import mock.domains.Dog;

import org.junit.Test;
import org.junit.runner.RunWith;


import integration.VertxMVCTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class FormParametersTest extends VertxMVCTestBase {

	@Test
	public void testFormString(TestContext context) {
		String myString = "Snoopy";
		Buffer data = Buffer.buffer();
		data.appendString("parameter=" + myString);
		Async async = context.async();
		client().post("/params/form/string").handler(response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(myString, buff.toString("UTF-8"));
                async.complete();
            });
		}).putHeader("content-type", "application/x-www-form-urlencoded").end(data);
	}
	
	/* etc. : all types : no point in testing, it's the same as query parameters */
	@Test
	public void testBackingParams(TestContext context) {
		Dog snoop = new Dog("Snoopy", "Beagle");
		Buffer data = Buffer.buffer();
		data.appendString("name=" + snoop.getName() + "&breed=" + snoop.getBreed());
		Async async = context.async();
		client().post("/params/form/dog").handler(response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(snoop.toString(), buff.toString("UTF-8"));
                async.complete();
            });
		}).putHeader("content-type", "application/x-www-form-urlencoded").end(data);		
	}
}
