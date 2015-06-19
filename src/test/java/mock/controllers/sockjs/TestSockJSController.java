package mock.controllers.sockjs;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

import com.github.aesteve.vertx.nubes.annotations.sockjs.OnClose;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnMessage;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnOpen;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;

@SockJS("/sockjs")
public class TestSockJSController {

    public static boolean opened = false; // very wrong since it's a singleton, but for testing purpose only

    @OnOpen
    public void openHandler(SockJSSocket socket) {
        opened = true;
    }

    @OnMessage
    public void messageHandler(SockJSSocket socket, Buffer buff) {
        socket.write(buff);
    }

    @OnClose
    public void closeHandler(SockJSSocket socket) {
        opened = false;
    }

}
