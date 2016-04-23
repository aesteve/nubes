package integration.api.pagination;

import com.google.common.net.HttpHeaders;
import integration.VertxNubesTestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class PaginationTest extends VertxNubesTestBase {

	// FIXME : 404 on this one, why ?

	// @Test
	// public void notPaginatedMethod(TestContext context) {
	// Async async = context.async();
	// getJSON("/pagination/notPaginated", response -> {
	// context.assertEquals(204, response.statusCode());
	// async.complete();
	// });
	// }

	@Test
	public void paginatedMethodWithSingleObject(TestContext context) {
		Async async = context.async();
		getJSON("/pagination/paginationContextAvailable", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertNull(response.headers().get(HttpHeaders.LINK));
			async.complete();
		});
	}

	@Test
	public void oneMiddlePage(TestContext context) {
		Async async = context.async();
		int perPage = 50;
		int page = 3;
		int total = 502;
		getJSON("/pagination/sendResults?nbResults=" + total + "&page=" + page + "&perPage=" + perPage, response -> {
			context.assertEquals(200, response.statusCode());
			context.assertNotNull(response.headers().get(HttpHeaders.LINK));
			response.bodyHandler(buff -> {
				JsonArray obj = new JsonArray(buff.toString("UTF-8"));
				context.assertNotNull(obj);
				context.assertEquals(perPage, obj.size());
				JsonObject dog = (JsonObject) obj.getValue(0);
				context.assertEquals("My name is dog number " + (perPage * (page - 1)) + " I wish I have a real name :'( ", dog.getString("name"));
				async.complete();
			});
		});
	}

	/**
	 * TODO : parseNavigationLinks and add more tests
	 * - firstPage / assert(First==null, Prev==null)
	 * - lastPage / assert(Last ==null, Next==null)
	 */
}
