package integration.custom;

import integration.CustomNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class ParamHandlerTest extends CustomNubesTestBase {

  @Test
  public void testParamHandlerIsCalled(TestContext context) {
    Async async = context.async();
    client().getNow("/custom/paramHandler", response -> {
      context.assertEquals(200, response.statusCode());
      context.assertNotNull(response.getHeader("X-Handler-Called"));
      async.complete();
    });
  }
}
