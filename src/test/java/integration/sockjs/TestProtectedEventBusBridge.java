package integration.sockjs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class TestProtectedEventBusBridge extends EventBusBridgeTestBase {

	public final static String INBOUND_PERM_1 = "inboundPermitted1";
	public final static String INBOUND_PERM_2 = "inboundPermitted1";
	public final static String OUTBOUND_PERM_1 = "outboundPermitted1";
	public final static String OUTBOUND_PERM_2 = "outboundPermitted1";

	@Test
	public void testInboundPermitted1(TestContext context) {
		testInboundPermitted(context, INBOUND_PERM_1);
	}

	@Test
	public void testInboundPermitted2(TestContext context) {
		testInboundPermitted(context, INBOUND_PERM_2);
	}

	@Test
	public void testOutboundPermitted1(TestContext context) {
		testOutboundPermitted(context, OUTBOUND_PERM_1);
	}

	@Test
	public void testOutboundPermitted2(TestContext context) {
		testOutboundPermitted(context, OUTBOUND_PERM_2);
	}

	@Test
	public void testInboundRefused(TestContext context) {
		String address = "notallowed";
		Async async = context.async();
		String msg = "It was a dark stormy night";
		vertx.eventBus().consumer(address, buff -> {
			context.fail("The message should not be forwarded to the event bus");
		});
		client().websocket("/eventbus/protected/websocket", socket -> {
			socket.handler(buffer -> {
				assertAccessRefused(context, buffer);
				async.complete();
			});
			publishThroughBridge(socket, address, msg);
		});
	}

	@Test
	public void testOutboundRefused(TestContext context) {
		String address = "notallowed";
		Async async = context.async();
		String msg = "It was a dark stormy night";
		client().websocket("/eventbus/protected/websocket", socket -> {
			socket.handler(buffer -> {
				assertAccessRefused(context, buffer);
				async.complete();
			});
			registerThroughBridge(socket, address, msg);
		});
	}

	private void testInboundPermitted(TestContext context, String address) {
		Async async = context.async();
		String msg = "It was a dark stormy night";
		vertx.eventBus().consumer(address, buff -> {
			context.assertEquals(msg, buff.body());
			async.complete();
		});
		client().websocket("/eventbus/protected/websocket", socket -> {
			publishThroughBridge(socket, address, msg);
		});
	}

	private void testOutboundPermitted(TestContext context, String address) {
		Async async = context.async();
		String msg = "It was a dark stormy night";
		client().websocket("/eventbus/protected/websocket", socket -> {
			socket.handler(buffer -> {
				JsonObject json = new JsonObject(buffer.toString("UTF-8"));
				assertEquals(msg, json.getString("body"));
				async.complete();
			});
			registerThroughBridge(socket, address, msg);
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
			vertx.eventBus().publish(address, msg);
		});
	}

	private void assertAccessRefused(TestContext context, Buffer buffer) {
		JsonObject resp = new JsonObject(buffer.toString("UTF-8"));
		context.assertEquals("err", resp.getString("type"));
		context.assertEquals("access_denied", resp.getString("body"));
	}
}
