package integration.sockjs;

import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;

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

}
