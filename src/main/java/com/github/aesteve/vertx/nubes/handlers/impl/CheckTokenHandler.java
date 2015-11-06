package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.AuthHandlerImpl;

public class CheckTokenHandler extends AuthHandlerImpl {

	public CheckTokenHandler(AuthProvider authProvider) {
		super(authProvider);
	}

	@Override
	public void handle(RoutingContext context) {
		User user = context.user();
		if (user != null) {
			authorise(user, context);
			return;
		}
		HttpServerRequest request = context.request();
		String apiToken;
		String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);
		if (authorization != null) {
			String[] parts = authorization.split(" ");
			String sscheme = parts[0];
			if (!"token".equals(sscheme)) {
				context.fail(400);
				return;
			}
			if (parts.length < 2) {
				context.fail(400);
				return;
			}
			apiToken = parts[1];
		} else {
			apiToken = request.getParam("access_token");
		}
		if (apiToken == null) {
			context.fail(401);
			return;
		}
		JsonObject authInfo = new JsonObject().put("access_token", apiToken);
		authProvider.authenticate(authInfo, res -> {
			if (res.succeeded()) {
				User authenticated = res.result();
				authenticated.setAuthProvider(authProvider);
				context.setUser(authenticated);
				authorise(authenticated, context);
			} else {
				context.fail(401);
			}
		});

	}
}
