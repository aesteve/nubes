package integration.shared;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class TestLocalMap extends VertxNubesTestBase {
    @Test
    public void testStaticMapValue(TestContext context) {
        Async async = context.async();
        String testValue = "test";
        vertx.sharedData().getLocalMap("test-map").put("key", testValue);
        client().getNow("/shared/local/staticValue", response -> {
            assertEquals(testValue, response.getHeader("X-Map-Value"));
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
            assertEquals(testValue, response.getHeader("X-Map-Value"));
            async.complete();
        });
    }
}
