package integration.server;

import org.junit.Test;

import integration.NubesServerTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.controllers.methods.SamePathDifferentMethodsController;

public class MethodsTest extends NubesServerTestBase {

	public static String path = "/testmethods/" + SamePathDifferentMethodsController.PATH;

	@Test
	public void testGet(TestContext context) {
		Async async = context.async();
		client().getNow(path, response -> {
			response.bodyHandler(buffer -> {
				context.assertEquals("GET", buffer.toString("UTF-8"));
				async.complete();
			});
		});
	}

}
