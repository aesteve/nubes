package mock.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

public class MockUser implements User {

	public String login;

	public MockUser(String login) {
		this.login = login;
	}

	@Override
	public User isAuthorised(String authority, Handler<AsyncResult<Boolean>> resultHandler) {
		Boolean authorized = authority.equals("") ? true : login.equals("tim");
		resultHandler.handle(Future.succeededFuture(authorized));
		return this;
	}

	@Override
	public User clearCache() {
		return this;
	}

	@Override
	public JsonObject principal() {
		JsonObject json = new JsonObject();
		json.put("login", login);
		return json;
	}

	@Override
	public void setAuthProvider(AuthProvider authProvider) {}

}
