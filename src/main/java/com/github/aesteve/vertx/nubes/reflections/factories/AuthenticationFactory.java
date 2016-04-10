package com.github.aesteve.vertx.nubes.reflections.factories;

import io.vertx.core.Handler;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import com.github.aesteve.vertx.nubes.handlers.impl.CheckTokenHandler;

public class AuthenticationFactory {

	private final Config config;

	public AuthenticationFactory(Config config) {
		this.config = config;
	}

	public Handler<RoutingContext> create(Auth auth) {
		final AuthProvider authProvider = config.getAuthProvider();
		if (authProvider == null) {
			return null;
		}
		final AuthMethod authMethod = auth.method();
		switch (authMethod) {
			case BASIC:
				return BasicAuthHandler.create(authProvider);
			case JWT:
				return JWTAuthHandler.create(authProvider);
			case REDIRECT:
				final String redirect = auth.redirectURL();
				if ("".equals(redirect)) {
					throw new IllegalArgumentException("You must specify a redirectURL if you're using Redirect Auth");
				}
				return RedirectAuthHandler.create(authProvider, redirect);
			case API_TOKEN:
				return new CheckTokenHandler(authProvider);
			default:
				throw new UnsupportedOperationException();
		}
	}
}
