package mock.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

public class MockUser implements User {

  public static boolean FLAGGED_AS_CLEARED = false;

  public String login;

  public MockUser(String login) {
    this.login = login;
  }

  @Override
  public User isAuthorized(String authority, Handler<AsyncResult<Boolean>> resultHandler) {
    Boolean authorized = "".equals(authority) || login.equals("tim");
    resultHandler.handle(Future.succeededFuture(authorized));
    return this;
  }

  @Override
  public User clearCache() {
    FLAGGED_AS_CLEARED = true;
    return this;
  }

  @Override
  public JsonObject principal() {
    JsonObject json = new JsonObject();
    json.put("login", login);
    return json;
  }

  @Override
  public void setAuthProvider(AuthProvider authProvider) {
  }

}
