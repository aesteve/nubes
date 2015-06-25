package integration.cookies;

import static io.vertx.core.http.HttpHeaders.COOKIE;
import static io.vertx.core.http.HttpHeaders.SET_COOKIE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class CookieTest extends VertxNubesTestBase {
	@Test
	public void testNoCookieValue(TestContext context) {
		Async async = context.async();
		client().getNow("/cookies/noCookie", response -> {
			assertNull(response.headers().get(SET_COOKIE));
			async.complete();
		});
	}

	@Test
	public void testSetCookieValue(TestContext context) {
		Async async = context.async();
		client().getNow("/cookies/setCookie", response -> {
			assertNotNull(response.headers().get(SET_COOKIE));
			async.complete();
		});
	}

	@Test
	public void testReadCookie(TestContext context) {
		String key = "dog";
		String value = "Cubitus";
		Async async = context.async();
		client().get("/cookies/echo", response -> {
			Buffer buff = Buffer.buffer();
			response.handler(buffer -> {
				buff.appendBuffer(buffer);
			});
			response.endHandler(handler -> {
				assertEquals(value, buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(COOKIE, key + "=" + value).end();
	}
}
