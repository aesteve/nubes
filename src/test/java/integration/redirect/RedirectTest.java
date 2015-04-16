package integration.redirect;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxMVCTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class RedirectTest extends VertxMVCTestBase {

	
	@Test
	public void serverRedirect(TestContext context) {
		Async async = context.async();
		client().getNow("/redirect/server", response -> {
			assertEquals(204, response.statusCode());
			assertNotNull(response.getHeader("afterredirect-beforefilter"));
			assertNotNull(response.getHeader("afterredirect-method"));
			assertNotNull(response.getHeader("afterredirect-afterfilter"));
			async.complete();
		});
	}
}
