package integration.views;

import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.domains.Dog;

import org.junit.Ignore;
import org.junit.Test;

public class ViewsTest extends VertxNubesTestBase {

	@Test
	@Ignore
	public void testSimpleHtml(TestContext context) {
		Async async = context.async();
		client().getNow("/views/default", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				String html = buffer.toString("UTF-8");
				context.assertTrue(html.indexOf("<html>") == 0);
				context.assertTrue(html.indexOf("</html>") > -1);
				context.assertTrue(html.indexOf("Australia") > -1);
				async.complete();
			});
		});
	}

	@Test
	public void testHandlebars(TestContext context) {
		Dog dog = TestVerticle.dogService.someDog();
		Async async = context.async();
		client().getNow("/views/handlebars?name=" + dog.getName() + "&breed=" + dog.getBreed(), response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				String result = buffer.toString("UTF-8");
				context.assertEquals("Hello! I'm a dog and my name is " + dog.getName(), result);
				async.complete();
			});
		});
	}

	@Test
	public void testDynamicViewName(TestContext context) {
		Dog dog = TestVerticle.dogService.someDog();
		Async async = context.async();
		client().getNow("/views/dynamic?name=" + dog.getName() + "&breed=" + dog.getBreed(), response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				String result = buffer.toString("UTF-8");
				context.assertEquals("Hello! I'm a dog and my name is " + dog.getName(), result);
				async.complete();
			});
		});
	}

	@Test
	public void testDynamicSync(TestContext context) {
		Dog dog = TestVerticle.dogService.someDog();
		Async async = context.async();
		client().getNow("/views/dynamic/sync?name=" + dog.getName() + "&breed=" + dog.getBreed(), response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				String result = buffer.toString("UTF-8");
				context.assertEquals("Hello! I'm a dog and my name is " + dog.getName(), result);
				async.complete();
			});
		});
	}

	@Test
	@Ignore
	// it's failing because we need a prefixed template engine for MVEL
	public void testMvel(TestContext context) {
		Dog dog = TestVerticle.dogService.someDog();
		Async async = context.async();
		client().getNow("/views/mvel?name=" + dog.getName() + "&breed=" + dog.getBreed(), response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				String result = buffer.toString("UTF-8");
				context.assertEquals("Hello! I'm a dog and my name is " + dog.getName(), result);
				async.complete();
			});
		});
	}
}
