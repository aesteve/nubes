package integration.sockjs;

import static org.junit.Assert.assertEquals;
import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public abstract class EventBusBridgeTestBase extends VertxNubesTestBase {

	protected static void registerThroughBridge(WebSocket ws, String address, String msg) {
		sendTypeToBridge(ws, "register", address, msg);
	}

	protected static void publishThroughBridge(WebSocket ws, String address, String msg) {
		sendTypeToBridge(ws, "publish", address, msg);
	}

	protected static void sendThroughBridge(WebSocket ws, String address, String msg) {
		sendTypeToBridge(ws, "send", address, msg);
	}

	protected static void sendTypeToBridge(WebSocket ws, String type, String address, String msg) {
		JsonObject json = new JsonObject();
		json.put("type", type);
		json.put("address", address);
		json.put("body", msg);
		ws.write(Buffer.buffer(json.toString()));
	}

	protected void testInboundPermitted(TestContext context, String wsAddress, String address) {
		Async async = context.async();
		String msg = "It was a dark stormy night";
		vertx.eventBus().consumer(address, buff -> {
			context.assertEquals(msg, buff.body());
			async.complete();
		});
		client().websocket(wsAddress, socket -> {
			publishThroughBridge(socket, address, msg);
		});
	}

	protected void testOutboundPermitted(TestContext context, String wsAddress, String address) {
		Async async = context.async();
		String msg = "It was a dark stormy night";
		client().websocket(wsAddress, socket -> {
			socket.handler(buffer -> {
				JsonObject json = new JsonObject(buffer.toString("UTF-8"));
				assertEquals(msg, json.getString("body"));
				async.complete();
			});
			registerThroughBridge(socket, address, msg);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			vertx.eventBus().publish(address, msg);
		});
	}

	protected void testInboundRefused(TestContext context, String wsAddress, String address) {
		Async async = context.async();
		String msg = "It was a dark stormy night";
		vertx.eventBus().consumer(address, buff -> {
			context.fail("The message should not be forwarded to the event bus");
		});
		client().websocket(wsAddress, socket -> {
			socket.handler(buffer -> {
				assertAccessRefused(context, buffer);
				async.complete();
			});
			publishThroughBridge(socket, address, msg);
		});
	}

	protected void testOutboundRefused(TestContext context, String wsAddress, String address) {
		Async async = context.async();
		String msg = "It was a dark stormy night";
		client().websocket(wsAddress, socket -> {
			socket.handler(buffer -> {
				assertAccessRefused(context, buffer);
				async.complete();
			});
			registerThroughBridge(socket, address, msg);
		});

	}

	protected void assertAccessRefused(TestContext context, Buffer buffer) {
		JsonObject resp = new JsonObject(buffer.toString("UTF-8"));
		context.assertEquals("err", resp.getString("type"));
		context.assertEquals("access_denied", resp.getString("body"));
	}

}
