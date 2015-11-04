package integration.sockjs;

import static io.vertx.ext.web.handler.sockjs.BridgeEventType.PUBLISH;
import static io.vertx.ext.web.handler.sockjs.BridgeEventType.RECEIVE;
import static io.vertx.ext.web.handler.sockjs.BridgeEventType.REGISTER;
import static io.vertx.ext.web.handler.sockjs.BridgeEventType.SEND;
import static io.vertx.ext.web.handler.sockjs.BridgeEventType.SOCKET_CLOSED;
import static io.vertx.ext.web.handler.sockjs.BridgeEventType.SOCKET_CREATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TestEventBusBridge extends EventBusBridgeTestBase {

	private final static String TEST_EB_ADDRESS = "test.address";

	@Test
	public void testSendRelatedEvents() throws Exception {
		CountDownLatch latch = new CountDownLatch(3);
		String msg = "Happiness is a warm puppy";
		vertx.eventBus().consumer(TEST_EB_ADDRESS, ebMsg -> {
			assertEquals(msg, ebMsg.body());
			latch.countDown();
		});
		vertx.eventBus().consumer(SOCKET_CREATED.toString(), ebMsg -> {
			assertEquals(SOCKET_CREATED.toString(), ebMsg.body());
			latch.countDown();
		});
		vertx.eventBus().consumer(SEND.toString(), ebMsg -> {
			assertEquals(SEND.toString(), ebMsg.body());
			latch.countDown();
		});
		client().websocket("/eventbus/default/websocket", ws -> {
			sendThroughBridge(ws, TEST_EB_ADDRESS, msg);
		});
		assertTrue(latch.await(2, TimeUnit.SECONDS));
	}

	@Test
	public void testPublishEvent(TestContext context) throws Exception {
		Async async = context.async();
		String msg = "Happiness is a warm puppy";
		vertx.eventBus().consumer(PUBLISH.toString(), ebMsg -> {
			context.assertEquals(PUBLISH.toString(), ebMsg.body());
			async.complete();
		});
		client().websocket("/eventbus/default/websocket", ws -> {
			publishThroughBridge(ws, TEST_EB_ADDRESS, msg);
		});
	}

	@Test
	public void testRegisterThenReceive() throws Exception {
		CountDownLatch latch = new CountDownLatch(3);
		String msg = "Happiness is a warm puppy";
		vertx.eventBus().consumer(REGISTER.toString(), ebMsg -> {
			assertEquals(REGISTER.toString(), ebMsg.body());
			latch.countDown();
		});
		vertx.eventBus().consumer(RECEIVE.toString(), ebMsg -> {
			assertEquals(RECEIVE.toString(), ebMsg.body());
			latch.countDown();
		});
		client().websocket("/eventbus/default/websocket", ws -> {
			registerThroughBridge(ws, TEST_EB_ADDRESS, msg);
			ws.handler(buff -> {
				JsonObject json = new JsonObject(buff.toString("UTF-8"));
				assertEquals(msg, json.getString("body"));
				latch.countDown();
			});

			try {
				Thread.sleep(500);/* let some time to handle the register event on server side */
			} catch (Exception e) {}
			vertx.eventBus().publish(TEST_EB_ADDRESS, msg);
		});
		assertTrue(latch.await(2, TimeUnit.SECONDS));
	}

	@Test
	public void testCloseEvent(TestContext context) throws Exception {
		Async async = context.async();
		vertx.eventBus().consumer(SOCKET_CLOSED.toString(), ebMsg -> {
			context.assertEquals(SOCKET_CLOSED.toString(), ebMsg.body());
			async.complete();
		});
		client().websocket("/eventbus/default/websocket", ws -> {
			ws.close();
		});
	}
}
