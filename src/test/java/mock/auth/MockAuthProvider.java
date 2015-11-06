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
		String userId = authInfo.getString("username");
		if (userId == null) {
			userId = authInfo.getString("access_token");
		}
		User user = new MockUser(userId);
		resultHandler.handle(Future.succeededFuture(user));
	}

}
