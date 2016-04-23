package unit;

import com.github.aesteve.vertx.nubes.utils.async.AsyncUtils;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(VertxUnitRunner.class)
public class AsyncTest {

  private final static Random rand = new Random();
  protected StringBuilder testSb;

  @Test
  public void testChanHandlers(TestContext context) {
    Async async = context.async();
    testSb = new StringBuilder();
    List<Handler<Future<Void>>> handlers = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      final int j = i;
      handlers.add(fut -> println(j, fut));
    }
    AsyncUtils.chainHandlers(res -> {
      context.assertEquals("0;1;2;3;4;5;6;7;8;9;", testSb.toString());
      async.complete();
    }, handlers);
  }

  private void println(int order, Future<Void> future) {
    testSb.append(order + ";");
    try {
      Thread.sleep(rand.nextInt(300));
    } catch (Exception e) {
    }
    future.complete();
  }
}
