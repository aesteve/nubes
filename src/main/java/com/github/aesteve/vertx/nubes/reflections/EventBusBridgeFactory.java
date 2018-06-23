package com.github.aesteve.vertx.nubes.reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.EventBusBridge;
import com.github.aesteve.vertx.nubes.reflections.annotations.ReflectionProviderHelper;
import com.github.aesteve.vertx.nubes.reflections.visitors.EventBusBridgeVisitor;
import io.vertx.ext.web.Router;
import org.reflections.Reflections;

import java.util.Set;

public class EventBusBridgeFactory implements HandlerFactory {

  private final Router router;
  private final Config config;

  public EventBusBridgeFactory(Router router, Config config) {
    this.router = router;
    this.config = config;
  }

  @Override
  public void createHandlers() {
    config.forEachControllerPackage(controllerPackage -> {
      Set<Class<?>> controllers = ReflectionProviderHelper.getAnnotationProcessor(config, controllerPackage).getClassesTypesAnnotatedWith(EventBusBridge.class);
      controllers.forEach(this::createSocketHandlers);
    });
  }

  private void createSocketHandlers(Class<?> controller) {
    EventBusBridgeVisitor<?> visitor = new EventBusBridgeVisitor<>(controller, config, router);
    visitor.visit();
  }

}
