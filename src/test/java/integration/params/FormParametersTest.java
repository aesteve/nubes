package integration.params;

import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.domains.Dog;
import org.junit.Test;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

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

  /* etc. : all types : no point in testing, it's the same as query parameters */
  @Test
  public void testBackingParamsWithBoolean(TestContext context) {
    Dog dog = TestVerticle.dogService.someDog();
    dog.setPuppy(true);
    Buffer data = Buffer.buffer();
    data.appendString("name=" + dog.getName() + "&breed=" + dog.getBreed() + "&puppy=" + dog.isPuppy());
    Async async = context.async();
    client().post("/params/form/dog").handler(response -> {
      response.bodyHandler(buff -> {
        context.assertEquals(dog.toString(), buff.toString("UTF-8"));
        async.complete();
      });
    }).putHeader(CONTENT_TYPE, "application/x-www-form-urlencoded").end(data);
  }

  @Test
  public void testBooleanParams(TestContext context) {
    Boolean value = true;
    Buffer data = Buffer.buffer();
    data.appendString("value=" + value);
    Async async = context.async();
    client().post("/params/form/boolean").handler(response -> {
      response.bodyHandler(buff -> {
        context.assertEquals(value, Boolean.valueOf(buff.toString()));
        async.complete();
      });
    }).putHeader(CONTENT_TYPE, "application/x-www-form-urlencoded").end(data);
  }

  @Test
  public void testFormBoolean(TestContext context) {
    Boolean myBoolean = true;
    Buffer data = Buffer.buffer();
    data.appendString("value=" + myBoolean);
    Async async = context.async();
    client().post("/params/form/boolean").handler(response -> {
      Buffer buff = Buffer.buffer();
      response.handler(buffer -> {
        buff.appendBuffer(buffer);
      });
      response.endHandler(handler -> {
        context.assertEquals(myBoolean, Boolean.valueOf(buff.toString()));
        async.complete();
      });
    }).putHeader(CONTENT_TYPE, "application/x-www-form-urlencoded").end(data);
  }
  @Test
  public void testWrongBackingParams(TestContext context) {
    Dog dog = TestVerticle.dogService.someDog();
    String invalidValue = "invalidLong";
    Buffer data = Buffer.buffer();
    data.appendString("name=" + dog.getName() + "&breed=" + dog.getBreed() + "&age=" + invalidValue);
    Async async = context.async();
    client().post("/params/form/dog").handler(response -> {
      context.assertEquals(400, response.statusCode());
      response.bodyHandler(buff -> {
        context.assertEquals("Some request of form parameter has an invalid value", buff.toString("UTF-8"));
        async.complete();
      });
    }).putHeader(CONTENT_TYPE, "application/x-www-form-urlencoded").end(data);
  }
}
