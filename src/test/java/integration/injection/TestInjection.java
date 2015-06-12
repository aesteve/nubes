package integration.injection;

import static org.junit.Assert.assertEquals;
import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestInjection extends VertxNubesTestBase {

    @Test
    public void dogIsSet(TestContext context) {
        Async async = context.async();
        getJSON("/injection/dog", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buff -> {
                System.out.println("received : " + buff.toString("UTF-8"));
                JsonObject json = new JsonObject(buff.toString("UTF-8"));
                String name = json.getString("name");
                assertEquals(TestVerticle.SNOOPY.getName(), name);
                async.complete();
            });
        });
    }
}
