package integration.routes;

import static org.junit.Assert.*;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestDisabled extends VertxNubesTestBase {

    @Test
    public void testEnabled(TestContext context) {
        Async async = context.async();
        client().getNow("/enabledController/enabledRoute", response -> {
            assertEquals(200, response.statusCode());
            async.complete();
        });
    }

    @Test
    public void testDisabled(TestContext context) {
        Async async = context.async();
        client().getNow("/enabledController/disabledRoute", response -> {
            assertEquals(404, response.statusCode());
            async.complete();
        });
    }

    @Test
    public void testDisabledCtrl(TestContext context) {
        Async async = context.async();
        client().getNow("/disabledController/route", response -> {
            assertEquals(404, response.statusCode());
            async.complete();
        });
    }

}
