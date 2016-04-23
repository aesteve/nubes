package integration.custom;

import integration.CustomNubesTestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class AdaptParam extends CustomNubesTestBase {
  @Test
  public void testAdaptParam(TestContext context) {
    String name = "as-request-param";
    Async async = context.async();
    getJSON("/custom/params/adapter?custom=" + name, response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        String result = buff.toString("UTF-8");
        JsonObject json = new JsonObject(result);
        context.assertEquals(name, json.getString("name"));
        async.complete();
      });
    });
  }
}
