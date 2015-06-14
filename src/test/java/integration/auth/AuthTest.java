package integration.auth;

import static org.junit.Assert.assertEquals;
import integration.VertxNubesTestBase;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class AuthTest extends VertxNubesTestBase {

	@Test
	public void test401(TestContext context) {
		Async async = context.async();
		client().getNow("/private/user", response -> {
			assertEquals(401, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testAuthorized(TestContext context) {
		Async async = context.async();
		client().get("/private/user", response -> {
			assertEquals(200, response.statusCode());
			async.complete();
		}).putHeader(HttpHeaders.AUTHORIZATION, getOKBearer()).end();
	}

	@Test
	public void testForbidden(TestContext context) {
		Async async = context.async();
		client().get("/private/admin", response -> {
			assertEquals(403, response.statusCode());
			async.complete();
		}).putHeader(HttpHeaders.AUTHORIZATION, getWrongBearer()).end();
	}

	private String getOKBearer() {
		return "Basic dGltOnNhdXNhZ2Vz";
	}

	private String getWrongBearer() {
		return "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
	}
}
