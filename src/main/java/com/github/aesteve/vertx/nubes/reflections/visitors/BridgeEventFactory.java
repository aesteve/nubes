package com.github.aesteve.vertx.nubes.reflections.visitors;

import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.*;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static io.vertx.ext.web.handler.sockjs.BridgeEventType.*;

class BridgeEventFactory {

  private static final Map<Class<? extends Annotation>, BridgeEventType> types = new HashMap<>();

  static {
    types.put(PUBLISH.class, PUBLISH);
    types.put(RECEIVE.class, RECEIVE);
    types.put(REGISTER.class, REGISTER);
    types.put(SEND.class, SEND);
    types.put(SOCKET_CLOSED.class, SOCKET_CLOSED);
    types.put(SOCKET_CREATED.class, SOCKET_CREATED);
    types.put(UNREGISTER.class, UNREGISTER);
  }

  public static Map<BridgeEventType, Method> createFromController(Class<?> controller) {
    Map<BridgeEventType, Method> map = new HashMap<>();
    for (Method method : controller.getMethods()) {
      for (Annotation annot : method.getDeclaredAnnotations()) {
        BridgeEventType type = types.get(annot.annotationType());
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
