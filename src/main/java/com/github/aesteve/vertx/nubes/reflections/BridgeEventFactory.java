package com.github.aesteve.vertx.nubes.reflections;

import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEvent.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.PUBLISH;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.RECEIVE;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.REGISTER;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.SEND;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.SOCKET_CLOSED;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.SOCKET_CREATED;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.UNREGISTER;

public class BridgeEventFactory {

	private static final Map<Class<? extends Annotation>, BridgeEvent.Type> types = new HashMap<>();
	static {
		types.put(PUBLISH.class, Type.PUBLISH);
		types.put(RECEIVE.class, Type.RECEIVE);
		types.put(REGISTER.class, Type.REGISTER);
		types.put(SEND.class, Type.SEND);
		types.put(SOCKET_CLOSED.class, Type.SOCKET_CLOSED);
		types.put(SOCKET_CREATED.class, Type.SOCKET_CREATED);
		types.put(UNREGISTER.class, Type.UNREGISTER);
	}

	public static Map<BridgeEvent.Type, Method> createFromController(Class<?> controller) {
		Map<BridgeEvent.Type, Method> map = new HashMap<>();
		for (Method method : controller.getMethods()) {
			for (Annotation annot : method.getDeclaredAnnotations()) {
				Type type = types.get(annot.annotationType());
				if (type != null) {
					if (map.get(type) != null) {
						throw new IllegalArgumentException("You cannot register many methods on the same BridgeEvent.Type");
					}
					map.put(type, method);
				}
			}
		}
		return map;
	}
}
