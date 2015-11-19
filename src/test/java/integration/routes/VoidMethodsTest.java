package integration.routes;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class VoidMethodsTest extends VertxNubesTestBase {

	@Test
	public void testVoidMethod(TestContext context) {
		Async async = context.async();
		getJSON("/void", response -> {
			context.assertEquals(204, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testWithResponse(TestContext context) {
		Async async = context.async();
		getJSON("/void/withResponse", response -> {
			context.assertEquals(200, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testWithAsyncResponse(TestContext context) {
		Async async = context.async();
		getJSON("/void/withAsyncResponse", response -> {
			context.assertEquals(200, response.statusCode());
			async.complete();
		});
	}
}
