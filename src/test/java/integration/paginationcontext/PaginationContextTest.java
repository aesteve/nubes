package integration.paginationcontext;

import integration.VertxNubesTestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class PaginationContextTest extends VertxNubesTestBase {

  @Test
  public void defaultPaginationContext(TestContext context) {
    Async async = context.async();
    getJSON("/paginationcontext", response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        JsonObject json = new JsonObject(buff.toString("UTF-8"));
        context.assertEquals(1, json.getInteger("current"));
        context.assertNull(json.getInteger("next"));
        context.assertNull(json.getInteger("last"));
        context.assertNull(json.getInteger("prev"));
        context.assertNull(json.getInteger("first"));
        context.assertEquals(1, json.getInteger("total"));
        async.complete();
      });
    });
  }

  @Test
  public void moreThanOnePage(TestContext context) {
    Async async = context.async();
    getJSON("/paginationcontext/more", response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        JsonObject json = new JsonObject(buff.toString("UTF-8"));
        context.assertEquals(1, json.getInteger("current"));
        context.assertEquals(2, json.getInteger("next"));
        context.assertEquals(11, json.getInteger("last"));
        context.assertNull(json.getInteger("prev"));
        context.assertNull(json.getInteger("first"));
        context.assertEquals(11, json.getInteger("total"));
        async.complete();
      });
    });
  }

  @Test
  public void moreThanOnePageInTheMiddle(TestContext context) {
    Async async = context.async();
    getJSON("/paginationcontext/more?page=3", response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        JsonObject json = new JsonObject(buff.toString("UTF-8"));
        context.assertEquals(3, json.getInteger("current"));
        context.assertEquals(4, json.getInteger("next"));
        context.assertEquals(11, json.getInteger("last"));
        context.assertEquals(2, json.getInteger("prev"));
        context.assertEquals(1, json.getInteger("first"));
        context.assertEquals(11, json.getInteger("total"));
        async.complete();
      });
    });
  }

  @Test
  public void moreThanOnePageAndCustomPerPage(TestContext context) {
    Async async = context.async();
    getJSON("/paginationcontext/more?page=2&perPage=100", response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        JsonObject json = new JsonObject(buff.toString("UTF-8"));
        context.assertEquals(2, json.getInteger("current"));
        context.assertEquals(3, json.getInteger("next"));
        context.assertEquals(4, json.getInteger("last"));
        context.assertEquals(1, json.getInteger("prev"));
        context.assertEquals(1, json.getInteger("first"));
        context.assertEquals(4, json.getInteger("total"));
        async.complete();
      });
    });
  }
}
