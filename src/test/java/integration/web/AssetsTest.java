package integration.web;

import static org.junit.Assert.assertEquals;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.controllers.assets.TestAssetsController;

import org.junit.Test;

/**
 * @README : Be careful : runnin this test from your IDE won't work
 *         Since the assets path is set as a relative path ("web/assets/") it can only work when ran from the root of the project
 */
public class AssetsTest extends VertxNubesTestBase {

	@Test
	public void rawAsset(TestContext context) throws Exception {
		Async async = context.async();
		client().getNow("/assets/hello.txt", response -> {
			assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				assertEquals("It was a dark stormy night...", buffer.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void instrumentedAsset(TestContext context) throws Exception {
		Async async = context.async();
		client().getNow("/assets/instrumented.txt", response -> {
			assertEquals(200, response.statusCode());
			assertEquals("yes", response.getHeader(TestAssetsController.INSTRUMENT_HEADER));
			response.bodyHandler(buffer -> {
				assertEquals("I should be instrumented", buffer.toString("UTF-8"));
				async.complete();
			});
		});

	}
}
