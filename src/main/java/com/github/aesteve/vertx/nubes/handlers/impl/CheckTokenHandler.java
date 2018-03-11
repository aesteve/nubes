package com.github.aesteve.vertx.nubes.handlers.impl;

import com.github.aesteve.vertx.nubes.exceptions.http.impl.BadRequestException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.AuthHandlerImpl;
import org.apache.commons.lang3.NotImplementedException;

public class CheckTokenHandler extends AuthHandlerImpl {

  public CheckTokenHandler(AuthProvider authProvider) {
    super(authProvider);
  }

  @Override
  public void handle(RoutingContext context) {
    User user = context.user();
    if (user != null) {
        authorize(user, event -> {});
      return;
    }
    String apiToken;
    try {
      apiToken = parseApiToken(context.request());
    } catch (BadRequestException bre) {
      context.fail(bre);
      return;
    }
    if (apiToken == null) {
      context.fail(401);
      return;
    }
    doAuth(context, apiToken);
  }

  private void doAuth(RoutingContext context, String apiToken) {
    JsonObject authInfo = new JsonObject().put("access_token", apiToken);
    authProvider.authenticate(authInfo, res -> {
      if (res.succeeded()) {
        User authenticated = res.result();
        authenticated.setAuthProvider(authProvider);
        context.setUser(authenticated);

          authorize(authenticated, event -> {});
      } else {
        context.fail(401);
      }
    });
  }

  private String parseApiToken(HttpServerRequest request) throws BadRequestException {
    String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);
    if (authorization != null) {
      String[] parts = authorization.split(" ");
      String sscheme = parts[0];
      if (!"token".equals(sscheme)) {
        throw new BadRequestException();
      }
      if (parts.length < 2) {
        throw new BadRequestException();
      }
      return parts[1];
    } else {
      return request.getParam("access_token");
    }

  }

  @Override
  public void parseCredentials(RoutingContext context, Handler<AsyncResult<JsonObject>> handler) {
    throw new NotImplementedException("praseCredentials has yet to be implemented");
  }
}
