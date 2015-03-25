package integration.api.pagination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import integration.VertxMVCTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class PaginationTest extends VertxMVCTestBase {

    public void notPaginatedMethod(TestContext context) {
        Async async = context.async();
        client().get("/pagination/notPaginated", response -> {
            assertEquals(204, response.statusCode());
            async.complete();
        }).putHeader("Accept", "application/json").end();
    }

    public void notPaginatedButUsingPagination(TestContext context) {
        Async async = context.async();
        client().get("/pagination/notPaginatedButUsingPagination", response -> {
            assertEquals(500, response.statusCode());
            async.complete();
        }).putHeader("Accept", "application/json").end();
    }

    public void paginatedMethodWithSingleObject(TestContext context) {
        Async async = context.async();
        client().get("/pagination/paginationContextAvailable", response -> {
            assertEquals(200, response.statusCode());
            assertNull(response.headers().get("Link"));
            async.complete();
        }).putHeader("Accept", "application/json").end();
    }

    @Test
    public void oneMiddlePage(TestContext context) {
        Async async = context.async();
        int perPage = 50;
        int page = 3;
        int total = 502;
        client().get("/pagination/sendResults?nbResults=" + total + "&page=" + page + "&perPage=" + perPage, response -> {
            assertEquals(200, response.statusCode());
            assertNotNull(response.headers().get("Link"));
            System.out.println(response.headers().get("Link"));
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                JsonArray obj = new JsonArray(buff.toString("UTF-8"));
                System.out.println(obj);
                assertNotNull(obj);
                assertEquals(perPage, obj.size());
                JsonObject dog = (JsonObject) obj.getValue(0);
                assertEquals("My name is dog number " + (perPage * (page - 1)) + " I wish I have a real name :'( ", dog.getString("name"));
                async.complete();
            });
        }).putHeader("Accept", "application/json").end();
    }

    /**
     * TODO : parseNavigationLinks and add more tests
     * - firstPage / assert(First==null, Prev==null)
     * - lastPage / assert(Last ==null, Next==null)
     */
}
