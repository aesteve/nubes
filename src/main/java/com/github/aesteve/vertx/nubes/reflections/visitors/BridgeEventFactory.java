package com.github.aesteve.vertx.nubes.reflections.visitors;

import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.events.*;
import io.vertx.ext.bridge.BridgeEventType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

abstract class BridgeEventFactory {

  private static final Map<Class<? extends Annotation>, BridgeEventType> types = new HashMap<>();

  private BridgeEventFactory() {}

  static {
    types.put(PUBLISH.class, BridgeEventType.PUBLISH);
    types.put(RECEIVE.class, BridgeEventType.RECEIVE);
    types.put(REGISTER.class, BridgeEventType.REGISTER);
    types.put(SEND.class, BridgeEventType.SEND);
    types.put(SOCKET_CLOSED.class, BridgeEventType.SOCKET_CLOSED);
    types.put(SOCKET_CREATED.class, BridgeEventType.SOCKET_CREATED);
    types.put(UNREGISTER.class, BridgeEventType.UNREGISTER);
  }

  static Map<BridgeEventType, Method> createFromController(Class<?> controller) {
    Map<BridgeEventType, Method> map = new EnumMap<>(BridgeEventType.class);
    for (Method method : controller.getMethods()) {
      createFromMethod(map, method);
    }
    return map;
  }

  private static void createFromMethod(Map<BridgeEventType, Method> map, Method method) {
    for (Annotation annot : method.getDeclaredAnnotations()) {
      createFromAnnotation(map, method, annot);
    }
  }

  private static void createFromAnnotation(Map<BridgeEventType, Method> map, Method method, Annotation annot) {
    BridgeEventType type = types.get(annot.annotationType());
    if (type != null && map.get(type) != null) {
      throw new IllegalArgumentException("You cannot register many methods on the same BridgeEvent.Type");
    } else if (type != null ){
      map.put(type, method);
    }
  }

}
