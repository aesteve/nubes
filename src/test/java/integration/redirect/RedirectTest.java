package integration.redirect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class RedirectTest extends VertxNubesTestBase {

	@Test
	public void serverRedirect(TestContext context) {
		Async async = context.async();
		client().getNow("/redirect/server", response -> {
			assertEquals(204, response.statusCode());
			assertNotNull(" before filter after redirect is called", response.getHeader("afterredirect-beforefilter"));
			assertNotNull(" main handler after redirect is called", response.getHeader("afterredirect-method"));
			assertNotNull(" after filter after redirect is called", response.getHeader("afterredirect-afterfilter"));
			async.complete();
		});
	}
}
