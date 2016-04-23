package integration.injection;

import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.domains.Dog;
import org.junit.Test;

public class TestInject extends VertxNubesTestBase {

  @Test
  public void testInjectService(TestContext context) {
    Async async = context.async();
    int i = 2;
    Dog dog = TestVerticle.dogService.getDog(i);
    getJSON("/inject/service?idx=" + i, response -> {
      response.bodyHandler(buff -> {
        JsonObject json = new JsonObject(buff.toString());
        context.assertEquals(dog.getName(), json.getString("name"));
        async.complete();
      });
    });
  }

  @Test
  public void testInjectClass(TestContext context) {
    Async async = context.async();
    Dog snoop = TestVerticle.SNOOPY;
    getJSON("/inject/class", response -> {
      response.bodyHandler(buff -> {
        JsonObject json = new JsonObject(buff.toString());
        context.assertEquals(snoop.getName(), json.getString("name"));
        async.complete();
      });
    });
  }

  @Test
  public void testInjectSocket(TestContext context) {
    Async async = context.async();
    Dog snoop = TestVerticle.SNOOPY;
    client().websocket("/injectedSocket/websocket", sock -> {
      sock.handler(buff -> {
        context.assertEquals(snoop.getName(), buff.toString("UTF-8"));
        async.complete();
      });
      sock.write(Buffer.buffer("something"));
    });
  }

  @Test
  public void testInjectRouter(TestContext context) {
    Async async = context.async();
    getJSON("/inject/router", response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        JsonObject json = new JsonObject(buff.toString("UTF-8"));
        context.assertNotNull(json.getString("router"));
        async.complete();
      });
    });
  }

  @Test
  public void testReadBodyAsJsonObject(TestContext context) {
    Async async = context.async();
    JsonObject snoop = new JsonObject().put("name", "Snoopy").put("breed", "Beagle");
    sendJSON("/inject/readBodyAsJsonObject", snoop, response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        context.assertEquals(snoop, new JsonObject(buff.toString("UTF-8")));
        async.complete();
      });
    });
  }

  @Test
  public void testReadBodyAsJsonArray(TestContext context) {
    Async async = context.async();
    JsonObject snoop = new JsonObject().put("name", "Snoopy").put("breed", "Beagle");
    JsonObject snowy = new JsonObject().put("name", "Snowy").put("breed", "Terrier");
    JsonArray dogs = new JsonArray();
    dogs.add(snoop).add(snowy);
    sendJSON("/inject/readBodyAsJsonArray", dogs, response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        context.assertEquals(dogs, new JsonArray(buff.toString("UTF-8")));
        async.complete();
      });
    });
  }

  @Test
  public void testWrongBodyAsJsonArray(TestContext context) {
    Async async = context.async();
    sendJSON("/inject/readBodyAsJsonArray", "{}", response -> {
      context.assertEquals(400, response.statusCode());
      async.complete();
    });
  }

  @Test
  public void testWrongBodyAsJsonObject(TestContext context) {
    Async async = context.async();
    sendJSON("/inject/readBodyAsJsonObject", "[]", response -> {
      context.assertEquals(400, response.statusCode());
      async.complete();
    });
  }
}
