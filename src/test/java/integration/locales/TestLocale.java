package integration.locales;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.unit.TestContext;

import static io.vertx.core.http.HttpHeaders.*;

@RunWith(VertxUnitRunner.class)
public class TestLocale extends VertxNubesTestBase {

    @Test
    public void perfectMatch(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("fr", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT_LANGUAGE, "fr, en;q=0.8, en-us;q=0.7").end();
    }

    @Test
    public void specificMatch(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("fr", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT_LANGUAGE, "fr-ca, en;q=0.8, en-us;q=0.7").end();
    }

    @Test
    public void anotherMatch(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("ja", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT_LANGUAGE, "de, ja-JP-u-ca-japanese;q=0.8, en;q=0.7, en-us;q=0.6").end();
    }

    @Test
    public void theLastOne(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("en", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT_LANGUAGE, "de, en;q=0.6").end();
    }

    @Test
    public void theDefaultOne(TestContext context) {
        Async async = context.async();
        client().get("/locales/echo", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("de", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT_LANGUAGE, "it, cz;q=0.6").end();
    }

    @Test
    public void greetMeInFrench(TestContext context) {
        Async async = context.async();
        client().get("/locales/greet", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("Bonjour", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT_LANGUAGE, "fr-ca, en;q=0.8, en-us;q=0.7").end();
    }

    @Test
    public void greetMeInDefault(TestContext context) {
        Async async = context.async();
        client().get("/locales/greet", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("Hallo", buffer.toString("UTF-8"));
                async.complete();
            });
        }).end();
    }

    @Test
    public void greetMeInDefault2(TestContext context) {
        Async async = context.async();
        client().get("/locales/greet", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("Hallo", buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT_LANGUAGE, "it, cz;q=0.6").end();
    }

}
