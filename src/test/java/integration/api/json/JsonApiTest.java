package integration.api.json;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import java.util.List;
import java.util.Map;

import mock.domains.Dog;

import org.junit.Test;

public class JsonApiTest extends VertxNubesTestBase {

	@Test
	public void noContentType(TestContext context) {
		Async async = context.async();
		client().getNow("/json/dog", response -> {
			context.assertEquals(406, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void wrongContentType(TestContext context) {
		Async async = context.async();
		client().get("/json/dog", response -> {
			context.assertEquals(406, response.statusCode());
			response.bodyHandler(buff -> {
				context.assertEquals("Not acceptable", buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT, "yourmum").end();
	}

	@Test
	public void getJsonObject(TestContext context) {
		Async async = context.async();
		getJSON("/json/jsonobject", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				JsonObject json = new JsonObject(buffer.toString("UTF-8"));
				context.assertNotNull(json);
				context.assertEquals(json.getString("Bill"), "Cocker");
				async.complete();
			});

		});
	}

	@Test
	public void getJsonArray(TestContext context) {
		Async async = context.async();
		getJSON("/json/jsonarray", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				JsonArray json = new JsonArray(buffer.toString("UTF-8"));
				context.assertNotNull(json);
				context.assertEquals(json.getJsonObject(0).getString("Bill"), "Cocker");
				async.complete();
			});

		});
	}

	@Test
	public void getMap(TestContext context) {
		Async async = context.async();
		getJSON("/json/map", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				JsonObject json = new JsonObject(buffer.toString("UTF-8"));
				context.assertNotNull(json);
				context.assertEquals(json.getString("Snoopy"), "Beagle");
				context.assertEquals(json.getString("Bill"), "Cocker");
				async.complete();
			});
		});
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getArray(TestContext context) {
		Async async = context.async();
		getJSON("/json/array", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				JsonArray json = new JsonArray(buffer.toString("UTF-8"));
				context.assertNotNull(json);
				List list = json.getList();
				context.assertEquals(list.get(0), "Snoopy");
				context.assertEquals(list.get(1), "Bill");
				async.complete();
			});
		});
	}

	@Test
	public void getDomainObject(TestContext context) {
		Async async = context.async();
		getJSON("/json/dog", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				JsonObject json = new JsonObject(buffer.toString("UTF-8"));
				context.assertNotNull(json);
				context.assertEquals(json.getString("name"), "Snoopy");
				context.assertEquals(json.getString("breed"), "Beagle");
				async.complete();
			});
		});
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getDomainObjects(TestContext context) {
		Async async = context.async();
		getJSON("/json/dogs", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				JsonArray json = new JsonArray(buffer.toString("UTF-8"));
				context.assertNotNull(json);
				List list = json.getList();
				context.assertEquals(((Map) list.get(0)).get("name"), "Snoopy");
				context.assertEquals(((Map) list.get(0)).get("breed"), "Beagle");
				context.assertEquals(((Map) list.get(1)).get("name"), "Bill");
				context.assertEquals(((Map) list.get(1)).get("breed"), "Cocker");
				async.complete();
			});
		});
	}

	@Test
	public void postSomeStuff(TestContext context) {
		Dog dog = TestVerticle.dogService.someDog();
		JsonObject dogJson = new JsonObject();
		dogJson.put("name", dog.getName());
		dogJson.put("breed", dog.getBreed());
		Async async = context.async();
		sendJSON("/json/postdog", dogJson, response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				String json = buffer.toString("UTF-8");
				JsonObject receivedDog = new JsonObject(json);
				context.assertEquals(dog.getName(), receivedDog.getString("name"));
				context.assertEquals(dog.getBreed(), receivedDog.getString("breed"));
				async.complete();
			});
		});
	}
	
	@Test
	public void testMarshalledError(TestContext context) {
		Async async = context.async();
		getJSON("/json/exception", response -> {
			context.assertEquals(500, response.statusCode());
			context.assertEquals("application/json", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buff -> {
				JsonObject json = new JsonObject(buff.toString());
				String msg = json.getJsonObject("error").getString("message");
				context.assertNotNull(msg);
				context.assertTrue(msg.startsWith("Exception : Manually thrown exception"));
				async.complete();
			});
		});
	}

	@Test
	public void test400(TestContext context) {
		testError(context, 400, "Bad request");
	}

	@Test
	public void test401(TestContext context) {
		testError(context, 401, "Unauthorized");
	}

	@Test
	public void test403(TestContext context) {
		testError(context, 403, "Forbidden");
	}

	@Test
	public void test404(TestContext context) {
		testError(context, 404, "Not found");
	}

	@Test
	public void test406(TestContext context) {
		testError(context, 406, "Not acceptable");
	}

	@Test
	public void test420(TestContext context) {
		testError(context, 420, "Rate limitation exceeded");
	}

	@Test
	public void test500(TestContext context) {
		testError(context, 500, "Internal server error");
	}

	@Test
	public void test503(TestContext context) {
		testError(context, 503, "Service temporarily unavailable");
	}

	private void testError(TestContext context, Integer statusCode, String expectedMessage) {
		Async async = context.async();
		getJSON("/json/fail/" + statusCode, response -> {
			context.assertEquals(statusCode.intValue(), response.statusCode());
			response.bodyHandler(buff -> {
				JsonObject json = new JsonObject(buff.toString());
				JsonObject error = json.getJsonObject("error");
				context.assertEquals(statusCode, error.getInteger("code"));
				context.assertEquals(expectedMessage, error.getString("message"));
				async.complete();
			});
		});

	}

}
