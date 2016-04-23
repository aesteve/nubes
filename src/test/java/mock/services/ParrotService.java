package mock.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
public interface ParrotService {

  void echo(String original, Handler<AsyncResult<String>> handler);

}
