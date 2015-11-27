package integration.cookies;

import static io.vertx.core.http.HttpHeaders.COOKIE;
import static io.vertx.core.http.HttpHeaders.SET_COOKIE;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class CookieTest extends VertxNubesTestBase {
	@Test
	public void testNoCookieValue(TestContext context) {
		Async async = context.async();
		client().getNow("/cookies/noCookie", response -> {
			context.assertNull(response.headers().get(SET_COOKIE));
			async.complete();
		});
	}

	@Test
	public void testSetCookieValue(TestContext context) {
		Async async = context.async();
		client().getNow("/cookies/setCookie", response -> {
			context.assertNotNull(response.headers().get(SET_COOKIE));
			async.complete();
		});
	}

	@Test
	public void testReadCookie(TestContext context) {
		String key = "dog";
		String value = "Cubitus";
		Async async = context.async();
		client().get("/cookies/echo", response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(value, buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(COOKIE, key + "=" + value).end();
	}

	@Test
	public void testReadCookieByName(TestContext context) {
		String key = "dog";
		String value = "Milou";
		Async async = context.async();
		client().get("/cookies/echoByName", response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(value, buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(COOKIE, key + "=" + value).end();
	}

	@Test
	public void testReadCookieObject(TestContext context) {
		String key = "dog";
		String value = "Milou";
		Async async = context.async();
		client().get("/cookies/echoObject", response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(value, buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(COOKIE, key + "=" + value).end();
	}

	@Test
	public void testCookieNotSet(TestContext context) {
		Async async = context.async();
		client().get("/cookies/echoObject", response -> {
			context.assertEquals(400, response.statusCode());
			async.complete();
		}).end();
	}
}
