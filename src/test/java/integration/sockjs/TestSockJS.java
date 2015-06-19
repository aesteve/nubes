package integration.sockjs;

import static org.junit.Assert.*;
import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import mock.controllers.sockjs.SockJSController;

import org.junit.Test;
import org.junit.runner.RunWith;
import static io.vertx.core.http.HttpHeaders.*;

@RunWith(VertxUnitRunner.class)
public class TestSockJS extends VertxNubesTestBase {

    @Test
    public void sockJSRunning(TestContext context) {
        Async async = context.async();
        client().getNow("/sockjs", response -> {
            assertEquals(200, response.statusCode());
            assertEquals("text/plain; charset=UTF-8", response.getHeader(CONTENT_TYPE.toString()));
            response.bodyHandler(buff -> {
                assertEquals("Welcome to SockJS!\n", buff.toString());
                async.complete();
            });
        });
    }

    @Test
    public void testSocketWorkflow(TestContext context) {
        Async async = context.async();
        client().websocket("/sockjs/websocket", ws -> {
            Buffer msg = Buffer.buffer("hello!");
            ws.handler(received -> {
                assertEquals(msg, received);
                ws.close();
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                assertFalse(SockJSController.opened);
                async.complete();
            });
            assertTrue(SockJSController.opened);
            ws.write(msg);
        });
    }
}
