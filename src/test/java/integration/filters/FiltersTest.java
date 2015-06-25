package integration.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
				assertEquals("before1;before2;before3;after1;after2;after3;", buffer.toString("UTF-8"));
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
			assertNotNull("Header before should've been set", before);
			assertNotNull("Header after should've been set", after);
			long timeBefore = Long.parseLong(before);
			long timeAfter = Long.parseLong(after);
			assertTrue("@After should be executed after @Before", timeAfter >= timeBefore);
			async.complete();
		});
	}
}
