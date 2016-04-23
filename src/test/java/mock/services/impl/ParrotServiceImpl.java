package mock.services.impl;

import com.github.aesteve.vertx.nubes.annotations.services.Proxify;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import mock.services.ParrotService;

@Proxify("service.parrot")
public class ParrotServiceImpl implements ParrotService {

	@Override
	public void echo(String original, Handler<AsyncResult<String>> handler) {
		handler.handle(Future.succeededFuture(original));
	}

}
