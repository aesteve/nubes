package integration.params;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxMVCTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

@RunWith(VertxUnitRunner.class)
public class RequestParametersTest extends VertxMVCTestBase {

    @Test
    public void testPathParam(TestContext context) {
        Async async = context.async();
        client().getNow("/params/path/dog/Snoopy", response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals("My name is : Snoopy", buff.toString("UTF-8"));
                async.complete();
            });
        });
    }
}
