package com.github.aesteve.vertx.nubes.reflections.visitors;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnClose;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnMessage;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnOpen;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;
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

public class SockJSVisitor<T> extends BaseVisitor<T> {

  private static final Logger LOG = LoggerFactory.getLogger(SockJSVisitor.class);

  private SockJSHandler sockJSHandler;
  private SockJS annot;
  private final List<Method> openHandlers;
  private final List<Method> messageHandlers;
  private final List<Method> closeHandlers;

  public SockJSVisitor(Class<T> clazz, Config config, Router router) {
    super(clazz, config, router);
    annot = clazz.getAnnotation(SockJS.class);
    path = annot.value();
    openHandlers = new ArrayList<>();
    messageHandlers = new ArrayList<>();
    closeHandlers = new ArrayList<>();
  }

  public void visit() {
    sockJSHandler = SockJSHandler.create(config.getVertx(), config.getSockJSOptions());
    try {
      instance = clazz.newInstance();
      injectServices();
    } catch (Exception e) {
      throw new VertxException("Could not instanciate socket controller : " + clazz.getName(), e);
    }
    createHandlers();
    sockJSHandler.socketHandler(ws -> {
      openHandlers.forEach(handler -> tryToInvoke(instance, handler, ws, null));
      ws.handler(buff -> messageHandlers.forEach(messageHandler -> tryToInvoke(instance, messageHandler, ws, buff)));
      ws.endHandler(voidz -> closeHandlers.forEach(closeHandler -> tryToInvoke(instance, closeHandler, ws, null)));
    });
    normalizePath();
    router.route(path).handler(sockJSHandler);
  }

  private void createHandlers() {
    for (Method method : clazz.getMethods()) {
      OnOpen openAnnot = method.getAnnotation(OnOpen.class);
      OnClose closeAnnot = method.getAnnotation(OnClose.class);
      OnMessage messageAnnot = method.getAnnotation(OnMessage.class);
      if (openAnnot != null) {
        openHandlers.add(method);
      }
      if (closeAnnot != null) {
        closeHandlers.add(method);
      }
      if (messageAnnot != null) {
        messageHandlers.add(method);
      }
    }
  }

  private void tryToInvoke(Object instance, Method method, SockJSSocket socket, Buffer msg) {
    try {
      method.invoke(instance, getParamValues(method, socket, msg));
    } catch (Exception e) {
      LOG.error("Error while handling websocket", e);
      socket.close();
    }
  }

  private Object[] getParamValues(Method method, SockJSSocket socket, Buffer msg) {
    final Vertx vertx = config.getVertx();
    List<Object> paramInstances = new ArrayList<>();
    for (Class<?> parameterClass : method.getParameterTypes()) {
      if (parameterClass.equals(SockJSSocket.class)) {
        paramInstances.add(socket);
      } else if (Buffer.class.isAssignableFrom(parameterClass)) {
        paramInstances.add(msg);
      } else if (parameterClass.equals(EventBus.class)) {
        paramInstances.add(vertx.eventBus());
      } else if (parameterClass.equals(Vertx.class)) {
        paramInstances.add(vertx);
      }
    }
    return paramInstances.toArray();
  }

}
