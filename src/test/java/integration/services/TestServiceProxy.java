package integration.services;

import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class TestServiceProxy extends VertxNubesTestBase {

  @Test
  public void testServiceIsProxified(TestContext context) {
    Async async = context.async();
    String msg = "No problem is too big to run away from";
    JsonObject json = new JsonObject();
    json.put("original", msg); // the name of the param as declared in the method, service-proxy is pretty clever
    DeliveryOptions options = new DeliveryOptions();
    options.addHeader("action", "echo");
    vertx.eventBus().send("service.parrot", json, options, reply -> {
      context.assertTrue(reply.succeeded());
      context.assertEquals(msg, reply.result().body());
      async.complete();
    });
  }

  @Test
  public void testServiceIsInjected(TestContext context) {
    String msg = "A whole stack of memories never equals one little hope";
    Async async = context.async();
    client().post("/injectedProxy", resp -> {
      context.assertEquals(200, resp.statusCode());
      resp.bodyHandler(buff -> {
        context.assertEquals(msg, buff.toString("UTF-8"));
        async.complete();
      });
    }).end(Buffer.buffer(msg));
  }

}
