package integration.api.throttling;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class ThrottlingTest extends VertxNubesTestBase {

	@Test
	public void singleRequest(TestContext context) {
		Async async = context.async();
		client().get("/throttling/dog", response -> {
			context.assertEquals(200, response.statusCode());
			async.complete();
		}).putHeader(ACCEPT, "application/json").end();
	}

	@Test
	public void twoRequests(TestContext context) {
		Async async = context.async();
		client().get("/throttling/dog", firstResponse -> {
			context.assertEquals(200, firstResponse.statusCode());
			client().get("/throttling/dog", secondResponse -> {
				context.assertEquals(200, secondResponse.statusCode());
				async.complete();
			}).putHeader(ACCEPT, "application/json").end();
		}).putHeader(ACCEPT, "application/json").end();
	}

	@Test
	public void threeRequests(TestContext context) {
		Async async = context.async();
		client().get("/throttling/dog", firstResponse -> {
			context.assertEquals(200, firstResponse.statusCode());
			client().get("/throttling/dog", secondResponse -> {
				context.assertEquals(200, secondResponse.statusCode());
				client().get("/throttling/dog", thirdResponse -> {
					context.assertEquals(420, thirdResponse.statusCode());
					async.complete();
				}).putHeader(ACCEPT, "application/json").end();
			}).putHeader(ACCEPT, "application/json").end();
		}).putHeader(ACCEPT, "application/json").end();
	}

	/**
	 * TODO : this must be the perfect example for using a testsuite instead of nested lambdas ??
	 */

	public void testAndWait(TestContext context) {
		Async async = context.async();
		client().get("/throttling/dog", firstResponse -> {
			context.assertEquals(200, firstResponse.statusCode());
			client().get("/throttling/dog", secondResponse -> {
				context.assertEquals(200, secondResponse.statusCode());
				client().get("/throttling/dog", thirdResponse -> {
					context.assertEquals(420, thirdResponse.statusCode());
					vertx.executeBlocking(future -> {
						try {
							Thread.sleep(10000);
							future.complete();
						} catch (Exception e) {
						}
					}, res -> {
						client().get("/throttling/dog", fourthResponse -> {
							context.assertEquals(200, fourthResponse.statusCode());
							async.complete();
						}).putHeader(ACCEPT, "application/json").end();
					});
				}).putHeader(ACCEPT, "application/json").end();
			}).putHeader(ACCEPT, "application/json").end();
		}).putHeader(ACCEPT, "application/json").end();
	}

	@Test
	public void publicRequests(TestContext context) {
		Async async = context.async();
		client().get("/throttling/notThrottled", firstResponse -> {
			context.assertEquals(200, firstResponse.statusCode());
			client().get("/throttling/notThrottled", secondResponse -> {
				context.assertEquals(200, secondResponse.statusCode());
				client().get("/throttling/dog", thirdResponse -> {
					context.assertEquals(200, thirdResponse.statusCode());
					async.complete();
				}).putHeader(ACCEPT, "application/json").end();
			}).putHeader(ACCEPT, "application/json").end();
		}).putHeader(ACCEPT, "application/json").end();
	}

	/**
	 * TODO : we MUST check that another client is not blocked by a first client
	 * TODO : how to forge a fake remoteHost for vertx.createClient() ?
	 */
}
