package integration.locales;

import static io.vertx.core.http.HttpHeaders.ACCEPT_LANGUAGE;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class TestLocale extends VertxNubesTestBase {

	@Test
	public void perfectMatch(TestContext context) {
		Async async = context.async();
		client().get("/locales/echo", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("fr", buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT_LANGUAGE, "fr, en;q=0.8, en-us;q=0.7").end();
	}

	@Test
	public void specificMatch(TestContext context) {
		Async async = context.async();
		client().get("/locales/echo", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("fr", buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT_LANGUAGE, "fr-ca, en;q=0.8, en-us;q=0.7").end();
	}

	@Test
	public void anotherMatch(TestContext context) {
		Async async = context.async();
		client().get("/locales/echo", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("ja", buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT_LANGUAGE, "de, ja-JP-u-ca-japanese;q=0.8, en;q=0.7, en-us;q=0.6").end();
	}

	@Test
	public void theLastOne(TestContext context) {
		Async async = context.async();
		client().get("/locales/echo", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("en", buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT_LANGUAGE, "de, en;q=0.6").end();
	}

	@Test
	public void theDefaultOne(TestContext context) {
		Async async = context.async();
		client().get("/locales/echo", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("de", buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT_LANGUAGE, "it, cz;q=0.6").end();
	}

	@Test
	public void greetMeInFrench(TestContext context) {
		Async async = context.async();
		client().get("/locales/greet", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("Bonjour", buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT_LANGUAGE, "fr-ca, en;q=0.8, en-us;q=0.7").end();
	}

	@Test
	public void greetMeInDefault(TestContext context) {
		Async async = context.async();
		client().get("/locales/greet", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("Hallo", buffer.toString("UTF-8"));
				async.complete();
			});
		}).end();
	}

	@Test
	public void greetMeInDefault2(TestContext context) {
		Async async = context.async();
		client().get("/locales/greet", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("Hallo", buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT_LANGUAGE, "it, cz;q=0.6").end();
	}

}
