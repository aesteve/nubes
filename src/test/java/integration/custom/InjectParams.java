package integration.custom;

import org.junit.Test;

import integration.CustomNubesTestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.custom.handlers.InjectObjectProcessor;

public class InjectParams extends CustomNubesTestBase {
	@Test
	public void objectByType(TestContext context) {
		Async async = context.async();
		getJSON("/custom/params", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buff -> {
				JsonObject json = new JsonObject(buff.toString("UTF-8"));
				context.assertEquals(new JsonObject().put("name", InjectObjectProcessor.obj.name), json);
				async.complete();
			});
		});
	}
	
	@Test
	public void objectByName(TestContext context) {
		Async async = context.async();
		getJSON("/custom/params/byName", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buff -> {
				JsonObject json = new JsonObject(buff.toString("UTF-8"));
				context.assertEquals(new JsonObject().put("name", "other-name"), json);
				async.complete();
			});
		});
	}
}
