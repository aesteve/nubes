package com.github.aesteve.vertx.nubes.reflections.factories;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BasicAuthHandler;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import io.vertx.ext.web.handler.FormLoginHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;

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
				if(auth.redirectURL()!="")
					return RedirectAuthHandler.create(config.authProvider, auth.redirectURL());
				return RedirectAuthHandler.create(config.authProvider,"/");
			default:
				throw new UnsupportedOperationException();
		}
	}
}
