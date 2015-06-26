package integration.redirect;

import static io.vertx.core.http.HttpHeaders.LOCATION;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.controllers.redirect.RedirectController;

import org.junit.Test;

public class RedirectTest extends VertxNubesTestBase {

	@Test
	public void clientRedirect(TestContext context) {
		Async async = context.async();
		client().getNow("/redirect/client", response -> {
			context.assertEquals(302, response.statusCode());
			String location = response.getHeader(LOCATION.toString());
			context.assertEquals(RedirectController.REDIRECT_LOCATION, location);
			async.complete();
		});
	}

	@Test
	public void clientRedirectWithCode(TestContext context) {
		Async async = context.async();
		client().getNow("/redirect/client/permanent", response -> {
			context.assertEquals(301, response.statusCode());
			String location = response.getHeader(LOCATION.toString());
			context.assertEquals(RedirectController.REDIRECT_LOCATION, location);
			async.complete();
		});
	}

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
