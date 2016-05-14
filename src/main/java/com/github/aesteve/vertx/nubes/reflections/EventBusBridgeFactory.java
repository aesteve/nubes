package com.github.aesteve.vertx.nubes.reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.EventBusBridge;
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
