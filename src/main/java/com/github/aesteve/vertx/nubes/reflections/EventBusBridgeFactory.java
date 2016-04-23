package com.github.aesteve.vertx.nubes.reflections;

import com.github.aesteve.vertx.nubes.reflections.visitors.EventBusBridgeVisitor;
import com.github.aesteve.vertx.nubes.reflections.visitors.SockJSVisitor;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.EventBusBridge;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.InboundPermitted;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.OutboundPermitted;

public class EventBusBridgeFactory implements HandlerFactory {
	private static final Logger LOG = LoggerFactory.getLogger(SocketFactory.class);

	private final Router router;
	private final Config config;

	public EventBusBridgeFactory(Router router, Config config) {
		this.router = router;
		this.config = config;
	}

	public void createHandlers() {
		config.forEachControllerPackage(controllerPackage -> {
			Reflections reflections = new Reflections(controllerPackage);
			Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(EventBusBridge.class);
			controllers.forEach(this::createSocketHandlers);
		});
	}

	private void createSocketHandlers(Class<?> controller) {
		EventBusBridgeVisitor<?> visitor = new EventBusBridgeVisitor<>(controller, config, router);
		visitor.visit();
	}

}
