package mock.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

public class MockAuthProvider implements AuthProvider {

	@Override
	public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
		User user = new MockUser(authInfo.getString("username"));
		resultHandler.handle(Future.succeededFuture(user));
	}

}
