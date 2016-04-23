package integration.session;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class SessionSpec extends VertxNubesTestBase {

  @Test
  public void getSession(TestContext context) {
    Async async = context.async();
    client().getNow("/api/session", response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        context.assertNotNull(buff.toString("UTF-8"));
        async.complete();
      });
    });
  }

}
