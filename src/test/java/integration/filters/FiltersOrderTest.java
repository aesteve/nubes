package integration.filters;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class FiltersOrderTest extends VertxNubesTestBase {

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
}
