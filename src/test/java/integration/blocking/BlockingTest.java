package integration.blocking;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class BlockingTest extends VertxNubesTestBase {

	@Test
	public void testBlocking(TestContext context) {
		Async async = context.async();
		long start = System.currentTimeMillis();
		client().getNow("/blocking/test", response -> {
			response.bodyHandler(buff -> {
				long end = System.currentTimeMillis();
				context.assertEquals("Done.", buff.toString());
				context.assertTrue(end - start > 3000);
				async.complete();
			});
		});
	}
}
