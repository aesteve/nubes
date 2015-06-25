package integration.sockjs;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import java.util.concurrent.atomic.AtomicBoolean;

import mock.controllers.sockjs.TestSockJSController;

import org.junit.Test;

public class TestSockJS extends VertxNubesTestBase {

	@Test
	public void sockJSRunning(TestContext context) {
		Async async = context.async();
		client().getNow("/sockjs", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("text/plain; charset=UTF-8", response.getHeader(CONTENT_TYPE.toString()));
			response.bodyHandler(buff -> {
				context.assertEquals("Welcome to SockJS!\n", buff.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSocketWorkflow(TestContext context) {
		Async async = context.async();
		AtomicBoolean openedCalled = new AtomicBoolean();
		vertx.eventBus().consumer(TestSockJSController.EB_ADDRESS, msg -> {
			String body = (String) msg.body();
			if ("opened".equals(body)) {
				openedCalled.set(true);
			} else if ("closed".equals(body)) {
				context.assertTrue(openedCalled.get());
				async.complete();
			}
		});
		client().websocket("/sockjs/websocket", ws -> {
			Buffer msg = Buffer.buffer("hello!");
			ws.handler(received -> {
				context.assertEquals(msg, received);
				ws.close();
			});
			ws.write(msg);
		});
	}
}
