package integration.filters;

import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class FiltersTest extends VertxNubesTestBase {

	@Test
	public void testOrderFilters(TestContext context) {
		Async async = context.async();
		client().getNow("/filters/order", response -> {
			response.bodyHandler(buffer -> {
				context.assertEquals("before1;before2;before3;after1;after2;after3;", buffer.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void testAOP(TestContext context) {
		Async async = context.async();
		client().getNow("/filters/aop", response -> {
			String before = response.getHeader(TestVerticle.HEADER_DATE_BEFORE);
			String after = response.getHeader(TestVerticle.HEADER_DATE_AFTER);
			context.assertNotNull("Header before should've been set", before);
			context.assertNotNull("Header after should've been set", after);
			long timeBefore = Long.parseLong(before);
			long timeAfter = Long.parseLong(after);
			context.assertTrue(timeAfter >= timeBefore, "@After should be executed after @Before");
			async.complete();
		});
	}
}
