package mock.services.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import mock.services.ParrotService;

import com.github.aesteve.vertx.nubes.annotations.services.Proxify;

@Proxify("service.parrot")
public class ParrotServiceImpl implements ParrotService {

	@Override
	public void echo(String original, Handler<AsyncResult<String>> handler) {
		handler.handle(Future.succeededFuture(original));
	}

}
