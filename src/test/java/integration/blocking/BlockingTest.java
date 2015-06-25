package integration.blocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
				assertEquals("Done.", buff.toString());
				assertTrue(end - start > 3000);
				/* TODO : how to check that no warning has been logged and that the event loop has not been blocked */
			});
			async.complete();
		});
	}
}
