package integration.file;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class FileTest extends VertxNubesTestBase {

    @Test
    public void getFile(TestContext context) {
        Async async = context.async();
        client().getNow("/file/txt", response -> {
            response.bodyHandler(buff -> {
                assertEquals("This is a text file", buff.toString("UTF-8"));
                async.complete();
            });
        });
    }

}
