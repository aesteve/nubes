package integration.auth;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

import com.google.common.net.HttpHeaders;

public class ApiAuthTest extends VertxNubesTestBase {

	public static final String VALID_TOKEN = "tim";

	@Test
	public void testWithNoToken(TestContext context) {
		Async async = context.async();
		getJSON("/private/api", response -> {
			context.assertEquals(401, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testValidTokenAsParam(TestContext context) {
		Async async = context.async();
		getJSON("/private/api?access_token=" + VALID_TOKEN, response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buff -> {
				context.assertEquals(VALID_TOKEN, buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void testValidTokenAsHeader(TestContext context) {
		Async async = context.async();
		client().get("/private/api?access_token=" + VALID_TOKEN, response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buff -> {
				context.assertEquals(VALID_TOKEN, buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(HttpHeaders.AUTHORIZATION, "token " + VALID_TOKEN).end();
	}

	@Test
	public void testInvalidTokenAsParam(TestContext context) {
		Async async = context.async();
		getJSON("/private/api?access_token=yourmum", response -> {
			context.assertEquals(403, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testInvalidTokenAsHeader(TestContext context) {
		Async async = context.async();
		client().get("/private/api?access_token=" + VALID_TOKEN, response -> {
			context.assertEquals(403, response.statusCode());
			async.complete();
		}).putHeader(HttpHeaders.AUTHORIZATION, "token yourmum").end();
	}
}
