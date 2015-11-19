package integration.exceptions;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class ExceptionsTest extends VertxNubesTestBase {

	@Test
	public void testBadRequest(TestContext context) {
		testException("badrequest", 400, context);
	}

	@Test
	public void testForbidden(TestContext context) {
		testException("forbidden", 403, context);
	}

	@Test
	public void testNotFound(TestContext context) {
		testException("notfound", 404, context);
	}

	@Test
	public void testUnauthorized(TestContext context) {
		testException("unauthorized", 401, context);
	}

	private void testException(String path, int expectedStatus, TestContext context) {
		Async async = context.async();
		getJSON("/exceptions/" + path, response -> {
			context.assertEquals(expectedStatus, response.statusCode());
			async.complete();
		});
	}

}
