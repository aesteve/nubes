package integration.failures;

import com.github.aesteve.vertx.nubes.VertxNubes;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class WrongServiceMethodsTest {

  protected Vertx vertx;
  protected VertxNubes nubes;

  private static JsonObject wrongPeriodicParams() {
    return new JsonObject().put("services", new JsonObject().put("wrong", "mock.broken.services.WrongPeriodicParams"));
  }

  private static JsonObject wrongConsumerParams() {
    return new JsonObject().put("services", new JsonObject().put("wrong", "mock.broken.services.WrongConsumerParams"));
  }

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
  }

  @After
  public void tearDown(TestContext ctx) {
    if (nubes == null) {
      return;
    }
    nubes.stop(ctx.asyncAssertSuccess());
  }

  @Test
  public void createNubesWithWrongServiceParams(TestContext context) {
    nubes = new VertxNubes(vertx, wrongPeriodicParams());
    Async async = context.async();
    nubes.bootstrap(res -> {
      context.assertTrue(res.failed());
      context.assertNotNull(res.cause());
      res.cause().printStackTrace();
      async.complete();
    });
  }

  @Test
  public void createNubesWithWrongServiceParams2(TestContext context) {
    nubes = new VertxNubes(vertx, wrongConsumerParams());
    Async async = context.async();
    nubes.bootstrap(res -> {
      context.assertTrue(res.failed());
      context.assertNotNull(res.cause());
      res.cause().printStackTrace();
      async.complete();
    });
  }
}
