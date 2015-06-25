package integration.shared;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class TestLocalMap extends VertxNubesTestBase {
	@Test
	public void testStaticMapValue(TestContext context) {
		Async async = context.async();
		String testValue = "test";
		vertx.sharedData().getLocalMap("test-map").put("key", testValue);
		client().getNow("/shared/local/staticValue", response -> {
			context.assertEquals(testValue, response.getHeader("X-Map-Value"));
			async.complete();
		});
	}

	@Test
	public void testDynamicMapValue(TestContext context) {
		Async async = context.async();
		String testValue = "test";
		String key = "somekey";
		vertx.sharedData().getLocalMap("test-map").put(key, testValue);
		client().getNow("/shared/local/dynamicValue?key=" + key, response -> {
			context.assertEquals(testValue, response.getHeader("X-Map-Value"));
			async.complete();
		});
	}
}
