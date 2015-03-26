package integration.api.throttling;

import static org.junit.Assert.assertEquals;
import integration.VertxMVCTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ThrottlingTest extends VertxMVCTestBase {

    @Test
    public void singleRequest(TestContext context) {
        Async async = context.async();
        client().get("/throttling/dog", response -> {
            assertEquals(200, response.statusCode());
            async.complete();
        }).putHeader("Accept", "application/json").end();
    }

    @Test
    public void twoRequests(TestContext context) {
        Async async = context.async();
        client().get("/throttling/dog", firstResponse -> {
            assertEquals(200, firstResponse.statusCode());
            client().get("/throttling/dog", secondResponse -> {
                assertEquals(200, secondResponse.statusCode());
                async.complete();
            }).putHeader("Accept", "application/json").end();
        }).putHeader("Accept", "application/json").end();
    }

    @Test
    public void threeRequests(TestContext context) {
        Async async = context.async();
        client().get("/throttling/dog", firstResponse -> {
            assertEquals(200, firstResponse.statusCode());
            client().get("/throttling/dog", secondResponse -> {
                assertEquals(200, secondResponse.statusCode());
                client().get("/throttling/dog", thirdResponse -> {
                    assertEquals(420, thirdResponse.statusCode());
                    async.complete();
                }).putHeader("Accept", "application/json").end();
            }).putHeader("Accept", "application/json").end();
        }).putHeader("Accept", "application/json").end();
    }

    /**
     * TODO : this must be the perfect example for using a testsuite instead of nested lambdas ??
     */
    @Test
    public void testAndWait(TestContext context) {
        Async async = context.async();
        client().get("/throttling/dog", firstResponse -> {
            assertEquals(200, firstResponse.statusCode());
            client().get("/throttling/dog", secondResponse -> {
                assertEquals(200, secondResponse.statusCode());
                client().get("/throttling/dog", thirdResponse -> {
                    assertEquals(420, thirdResponse.statusCode());
                    vertx.executeBlocking(future -> {
                        try {
                            Thread.sleep(10000);
                            future.complete();
                        } catch (Exception e) {
                        }
                    }, res -> {
                        client().get("/throttling/dog", fourthResponse -> {
                            assertEquals(200, fourthResponse.statusCode());
                            async.complete();
                        }).putHeader("Accept", "application/json").end();
                    });
                }).putHeader("Accept", "application/json").end();
            }).putHeader("Accept", "application/json").end();
        }).putHeader("Accept", "application/json").end();
    }

    @Test
    public void publicRequests(TestContext context) {
        Async async = context.async();
        client().get("/throttling/notThrottled", firstResponse -> {
            assertEquals(200, firstResponse.statusCode());
            client().get("/throttling/notThrottled", secondResponse -> {
                assertEquals(200, secondResponse.statusCode());
                client().get("/throttling/dog", thirdResponse -> {
                    assertEquals(200, thirdResponse.statusCode());
                    async.complete();
                }).putHeader("Accept", "application/json").end();
            }).putHeader("Accept", "application/json").end();
        }).putHeader("Accept", "application/json").end();
    }

    /**
     * TODO : we MUST check that another client is not blocked by a first client
     * TODO : how to forge a fake remoteHost for vertx.createClient() ?
     */
}