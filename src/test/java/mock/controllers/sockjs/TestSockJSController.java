package mock.controllers.sockjs;

import com.github.aesteve.vertx.nubes.annotations.sockjs.OnClose;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnMessage;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnOpen;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

@SockJS("/sockjs")
public class TestSockJSController {

	public final static String EB_ADDRESS = "sockjs-test";

	@OnOpen
	public void openHandler(SockJSSocket socket, EventBus eventBus) {
		eventBus.publish(EB_ADDRESS, "opened");
	}

	@OnMessage
	public void messageHandler(SockJSSocket socket, Buffer buff) {
		socket.write(buff);
	}

	@OnClose
	public void closeHandler(SockJSSocket socket, EventBus eventBus) {
		eventBus.publish(EB_ADDRESS, "closed");
	}

}
