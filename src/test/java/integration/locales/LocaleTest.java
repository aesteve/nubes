package integration.locales;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.unit.TestContext;

@RunWith(VertxUnitRunner.class)
public class LocaleTest extends VertxNubesTestBase {

    @Test
    public void perfectMatch(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(response.statusCode(), 200);
            response.bodyHandler(buffer -> {
                assertEquals("fr", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader("Accept-Language", "fr, en;q=0.8, en-us;q=0.7").end();
    }

    @Test
    public void specificMatch(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(response.statusCode(), 200);
            response.bodyHandler(buffer -> {
                assertEquals("fr", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader("Accept-Language", "fr-ca, en;q=0.8, en-us;q=0.7").end();
    }

    @Test
    public void anotherMatch(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(response.statusCode(), 200);
            response.bodyHandler(buffer -> {
                assertEquals("ja", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader("Accept-Language", "de, ja-JP-u-ca-japanese;q=0.8, en;q=0.7, en-us;q=0.6").end();
    }

    @Test
    public void theLastOne(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(response.statusCode(), 200);
            response.bodyHandler(buffer -> {
                assertEquals("en", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader("Accept-Language", "de, en;q=0.6").end();
    }

    @Test
    public void theDefaultOne(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(response.statusCode(), 200);
            response.bodyHandler(buffer -> {
                assertEquals("de", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader("Accept-Language", "it, cz;q=0.6").end();
    }

}
