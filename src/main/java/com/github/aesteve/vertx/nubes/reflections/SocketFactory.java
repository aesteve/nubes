package com.github.aesteve.vertx.nubes.reflections;

import com.github.aesteve.vertx.nubes.reflections.visitors.SockJSVisitor;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnClose;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnMessage;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnOpen;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;

public class SocketFactory implements HandlerFactory {

	private static final Logger LOG = LoggerFactory.getLogger(SocketFactory.class);

	private final Router router;
	private final Config config;

	public SocketFactory(Router router, Config config) {
		this.router = router;
		this.config = config;
	}

	public void createHandlers() {
		config.forEachControllerPackage(controllerPackage -> {
			Reflections reflections = new Reflections(controllerPackage);
			Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(SockJS.class);
			controllers.forEach(this::createSocketHandlers);
		});
	}

	private<T> void createSocketHandlers(Class<T> controller) {
		SockJSVisitor<T> visitor = new SockJSVisitor<>(controller, config, router);
		visitor.visit();
	}

}
