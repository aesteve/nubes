package integration.routes;

import static org.junit.Assert.*;

import org.junit.Test;

import integration.VertxMVCTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class RenderStringTest extends VertxMVCTestBase {
    @Test
    public void simplePath(TestContext context) {
        Async async = context.async();
        client().getNow("/hello", response -> {
            assertEquals(200, response.statusCode());
            response.handler(buffer -> {
                assertEquals(buffer.toString("UTF-8"), "Hello world!");
                async.complete();
            });
        });
    }

    @Test
    public void nestedPath(TestContext context) {
        Async async = context.async();
        client().getNow("/base/test", response -> {
            assertEquals(200, response.statusCode());
            response.handler(buffer -> {
                assertEquals(buffer.toString("UTF-8"), "/base/test");
                async.complete();
            });
        });
    }
}