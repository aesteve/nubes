package com.github.aesteve.vertx.nubes.reflections.factories;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;

public class AuthenticationFactory {

	private Config config;

	public AuthenticationFactory(Config config) {
		this.config = config;
	}

	public Handler<RoutingContext> create(Auth auth) {
		if (config.authProvider == null) {
			return null;
		}
		AuthMethod authMethod = config.authMethod;
		if (auth.method() != null) {
			authMethod = auth.method();
		}
		switch (authMethod) {
			case BASIC:
				return BasicAuthHandler.create(config.authProvider);
			case JWT:
				return JWTAuthHandler.create(config.authProvider);
			case REDIRECT:
				final String redirect = auth.redirectURL();
				if ("".equals(redirect)) {
					throw new IllegalArgumentException("You must specify a redirectURL if you're using Redirect Auth");
				}
				return RedirectAuthHandler.create(config.authProvider, redirect);
			default:
				throw new UnsupportedOperationException();
		}
	}
}
