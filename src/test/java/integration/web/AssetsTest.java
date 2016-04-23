package integration.web;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.controllers.assets.TestAssetsController;
import org.junit.Test;

public class AssetsTest extends VertxNubesTestBase {

  @Test
  public void rawAsset(TestContext context) throws Exception {
    Async async = context.async();
    client().getNow("/assets/hello.txt", response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buffer -> {
        context.assertEquals("It was a dark stormy night...", buffer.toString("UTF-8"));
        async.complete();
      });
    });
  }

  @Test
  public void instrumentedAsset(TestContext context) throws Exception {
    Async async = context.async();
    client().getNow("/assets/instrumented.txt", response -> {
      context.assertEquals(200, response.statusCode());
      context.assertEquals("yes", response.getHeader(TestAssetsController.INSTRUMENT_HEADER));
      response.bodyHandler(buffer -> {
        context.assertEquals("I should be instrumented", buffer.toString("UTF-8"));
        async.complete();
      });
    });

  }
}
