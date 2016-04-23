package integration.custom;

import integration.CustomNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.custom.controllers.ErrorHandlerTestController;
import org.junit.Test;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class ErrorHandlerTest extends CustomNubesTestBase {
  @Override
  protected boolean hideErrors() {
    return true;
  }

  @Test
  public void testErrorHandler(TestContext ctx) {
    Async async = ctx.async();
    client().get("/custom/errorHandler", response -> {
      ctx.assertEquals(500, response.statusCode());
      response.bodyHandler(buff -> {
        ctx.assertEquals(ErrorHandlerTestController.EXCEPTION_MSG, buff.toString("UTF-8"));
        async.complete();
      });
    }).putHeader(ACCEPT, "text/plain").putHeader(CONTENT_TYPE, "text/plain").end();
  }
}
