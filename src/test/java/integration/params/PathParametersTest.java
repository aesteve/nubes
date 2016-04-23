package integration.params;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.controllers.params.PathParametersTestController.Animal;
import org.junit.Test;

public class PathParametersTest extends VertxNubesTestBase {

  @Test
  public void testStringParam(TestContext context) {
    String myString = "Snoopy";
    Async async = context.async();
    client().getNow("/params/path/string/" + myString, response -> {
      response.bodyHandler(buff -> {
        context.assertEquals(myString, buff.toString("UTF-8"));
        async.complete();
      });
    });
  }

  @Test
  public void testLongParam(TestContext context) {
    Long myLong = 1234l;
    Async async = context.async();
    client().getNow("/params/path/long/" + myLong, response -> {
      response.bodyHandler(buff -> {
        context.assertEquals(myLong.toString(), buff.toString("UTF-8"));
        async.complete();
      });
    });
  }

  @Test
  public void testIntParam(TestContext context) {
    Integer myInt = 123;
    Async async = context.async();
    client().getNow("/params/path/int/" + myInt, response -> {
      response.bodyHandler(buff -> {
        context.assertEquals(myInt.toString(), buff.toString("UTF-8"));
        async.complete();
      });
    });
  }

  @Test
  public void testEnum(TestContext context) {
    Animal animal = Animal.DOG;
    Async async = context.async();
    client().getNow("/params/path/enum/" + animal, response -> {
      response.bodyHandler(buff -> {
        context.assertEquals(animal.toString(), buff.toString("UTF-8"));
        async.complete();
      });
    });
  }

  @Test
  public void testPathParam(TestContext context) {
    String value = "Droopy";
    Async async = context.async();
    client().getNow("/params/path/byName/" + value, response -> {
      response.bodyHandler(buff -> {
        context.assertEquals(value, buff.toString("UTF-8"));
        async.complete();
      });
    });
  }

}
