package integration.routes;

import static org.junit.Assert.*;

import org.junit.Test;

import integration.VertxMVCTestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class FiltersTest extends VertxMVCTestBase {
	
	@Test
	public void testFiltered1(TestContext context){
		Async async = context.async();
		client().getNow("/filtered1", response -> {
			assertEquals(response.statusCode(), 200);
			response.handler(buffer -> {
				JsonObject obj = new JsonObject(buffer.toString("UTF-8"));
				assertNotNull(obj);
				assertTrue(obj.getBoolean("BeforeFilter1"));
				assertTrue(obj.getBoolean("BeforeFilter2"));
				assertTrue(obj.getBoolean("AfterFilter1"));
				assertTrue(obj.getBoolean("AfterFilter2"));
				assertTrue(obj.getBoolean("Filtered1"));
				assertNull(obj.getBoolean("Filtered2"));
				async.complete();
			});
		});
	}
	
	@Test
	public void testFiltered2(TestContext context){
		Async async = context.async();
		client().getNow("/filtered2", response -> {
			assertEquals(response.statusCode(), 200);
			response.handler(buffer -> {
				JsonObject obj = new JsonObject(buffer.toString("UTF-8"));
				assertNotNull(obj);
				assertTrue(obj.getBoolean("BeforeFilter1"));
				assertTrue(obj.getBoolean("BeforeFilter2"));
				assertTrue(obj.getBoolean("AfterFilter1"));
				assertTrue(obj.getBoolean("AfterFilter2"));
				assertTrue(obj.getBoolean("Filtered2"));
				assertNull(obj.getBoolean("Filtered1"));
				async.complete();
			});
		});
	}
}
