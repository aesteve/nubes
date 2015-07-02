package integration.params;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.domains.Dog;

import org.junit.Test;

public class FormParametersTest extends VertxNubesTestBase {

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
				context.assertEquals(myString, buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(CONTENT_TYPE, "application/x-www-form-urlencoded").end(data);
	}

	/* etc. : all types : no point in testing, it's the same as query parameters */
	@Test
	public void testBackingParams(TestContext context) {
		Dog dog = TestVerticle.dogService.someDog();
		Buffer data = Buffer.buffer();
		data.appendString("name=" + dog.getName() + "&breed=" + dog.getBreed());
		Async async = context.async();
		client().post("/params/form/dog").handler(response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(dog.toString(), buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(CONTENT_TYPE, "application/x-www-form-urlencoded").end(data);
	}
}
