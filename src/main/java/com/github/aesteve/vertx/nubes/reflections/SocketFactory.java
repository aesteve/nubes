package com.github.aesteve.vertx.nubes.reflections;

import io.vertx.core.buffer.Buffer;
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

public class SocketFactory {

    private static final Logger log = LoggerFactory.getLogger(SocketFactory.class);

    private Router router;
    private Config config;

    public SocketFactory(Router router, Config config) {
        this.router = router;
        this.config = config;
    }

    public void createSocketHandlers() {
        config.controllerPackages.forEach(controllerPackage -> {
            Reflections reflections = new Reflections(controllerPackage);
            Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(SockJS.class);
            controllers.forEach(controller -> {
                createSocketHandlers(controller);
            });
        });
    }

    private void createSocketHandlers(Class<?> controller) {
        SockJSHandler sockJSHandler = SockJSHandler.create(config.vertx, config.sockJSOptions);
        SockJS annot = controller.getAnnotation(SockJS.class);
        String path = annot.value();
        List<Method> openHandlers = new ArrayList<Method>();
        List<Method> messageHandlers = new ArrayList<Method>();
        List<Method> closeHandlers = new ArrayList<Method>();
        Object ctrlInstance = null;
        try {
            ctrlInstance = controller.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instanciate socket controller : " + controller.getName(), e);
        }
        final Object instance = ctrlInstance;
        for (Method method : controller.getMethods()) {
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
        sockJSHandler.socketHandler(ws -> {
            openHandlers.forEach(handler -> {
                tryToInvoke(instance, handler, ws, null);
            });
            ws.handler(buff -> {
                messageHandlers.forEach(messageHandler -> {
                    tryToInvoke(instance, messageHandler, ws, buff);
                });
            });
            ws.endHandler(voidz -> {
                closeHandlers.forEach(closeHandler -> {
                    tryToInvoke(instance, closeHandler, ws, null);
                });
            });
        });
        System.out.println("mounting sockJSHandler on path : " + path);
        router.route(path).handler(sockJSHandler);
    }

    private void tryToInvoke(Object instance, Method method, SockJSSocket socket, Buffer msg) {
        List<Object> paramInstances = new ArrayList<Object>(2);
        for (Class<?> parameterClass : method.getParameterTypes()) {
            if (parameterClass.isAssignableFrom(SockJSSocket.class)) {
                paramInstances.add(socket);
            } else if (parameterClass.isAssignableFrom(Buffer.class)) {
                paramInstances.add(msg);
            }
        }
        try {
            method.invoke(instance, paramInstances.toArray());
        } catch (Exception e) {
            log.error("Error while handling websocket", e);
        }
    }
}
