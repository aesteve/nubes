package integration.routes;

import integration.VertxNubesTestBase;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.controllers.methods.SamePathDifferentMethodsController;
import org.junit.Test;

public class MethodsTest extends VertxNubesTestBase {

  public static String path = "/testmethods/" + SamePathDifferentMethodsController.PATH;

  @Test
  public void testGet(TestContext context) {
    Async async = context.async();
    client().getNow(path, response -> {
      response.bodyHandler(buffer -> {
        context.assertEquals("GET", buffer.toString("UTF-8"));
        async.complete();
      });
    });
  }

  @Test
  public void testPost(TestContext context) {
    Async async = context.async();
    client().post(path, response -> {
      response.bodyHandler(buffer -> {
        context.assertEquals("POST", buffer.toString("UTF-8"));
        async.complete();
      });
    }).end();
  }

  @Test
  public void testPut(TestContext context) {
    Async async = context.async();
    client().put(path, response -> {
      response.bodyHandler(buffer -> {
        context.assertEquals("PUT", buffer.toString("UTF-8"));
        async.complete();
      });
    }).end();
  }

  @Test
  public void testPatch(TestContext context) {
    Async async = context.async();
    client().request(HttpMethod.PATCH, path, response -> {
      response.bodyHandler(buffer -> {
        context.assertEquals("PUT", buffer.toString("UTF-8"));
        async.complete();
      });
    }).end();
  }

  @Test
  public void testOptions(TestContext context) {
    Async async = context.async();
    client().options(path, response -> {
      response.bodyHandler(buffer -> {
        context.assertEquals("OPTIONS", buffer.toString("UTF-8"));
        async.complete();
      });
    }).end();
  }

  @Test
  public void testDelete(TestContext context) {
    Async async = context.async();
    client().delete(path, response -> {
      response.handler(buffer -> {
        context.assertEquals("DELETE", buffer.toString("UTF-8"));
        async.complete();
      });
    }).end();
  }

}
