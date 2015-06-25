package integration.redirect;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class RedirectTest extends VertxNubesTestBase {

	@Test
	public void serverRedirect(TestContext context) {
		Async async = context.async();
		client().getNow("/redirect/server", response -> {
			context.assertEquals(204, response.statusCode());
			context.assertNotNull(" before filter after redirect is called", response.getHeader("afterredirect-beforefilter"));
			context.assertNotNull(" main handler after redirect is called", response.getHeader("afterredirect-method"));
			context.assertNotNull(" after filter after redirect is called", response.getHeader("afterredirect-afterfilter"));
			async.complete();
		});
	}
}
