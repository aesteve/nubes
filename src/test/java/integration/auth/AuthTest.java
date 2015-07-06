package integration.auth;

import integration.VertxNubesTestBase;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class AuthTest extends VertxNubesTestBase {

	@Test
	public void test401(TestContext context) {
		Async async = context.async();
		client().getNow("/private/user", response -> {
			context.assertEquals(401, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testAuthorized(TestContext context) {
		Async async = context.async();
		client().get("/private/user", response -> {
			context.assertEquals(200, response.statusCode());
			async.complete();
		}).putHeader(HttpHeaders.AUTHORIZATION, getOKBearer()).end();
	}

	@Test
	public void testForbidden(TestContext context) {
		Async async = context.async();
		client().get("/private/admin", response -> {
			context.assertEquals(403, response.statusCode());
			async.complete();
		}).putHeader(HttpHeaders.AUTHORIZATION, getWrongBearer()).end();
	}

	private static String getOKBearer() {
		return "Basic dGltOnNhdXNhZ2Vz";
	}

	private static String getWrongBearer() {
		return "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
	}
}
