package mock.controllers.injection;

import integration.TestVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;
import mock.domains.Dog;

import com.github.aesteve.vertx.nubes.annotations.services.Service;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnMessage;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;

@SockJS("/injectedSocket/*")
public class TestInjectedSock {

	@Service(TestVerticle.SNOOPY_SERVICE_NAME)
	private Dog snoop;

	@OnMessage
	public void getDog(Buffer msg, SockJSSocket sock) {
		sock.write(Buffer.buffer(snoop.getName()));
	}
}
