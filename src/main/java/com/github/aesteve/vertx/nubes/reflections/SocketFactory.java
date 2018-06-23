package com.github.aesteve.vertx.nubes.reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;
import com.github.aesteve.vertx.nubes.reflections.annotations.ReflectionProviderHelper;
import com.github.aesteve.vertx.nubes.reflections.visitors.SockJSVisitor;
import io.vertx.ext.web.Router;
import org.reflections.Reflections;

import java.util.Set;

public class SocketFactory implements HandlerFactory {

  private final Router router;
  private final Config config;

  public SocketFactory(Router router, Config config) {
    this.router = router;
    this.config = config;
  }

  @Override
  public void createHandlers() {
    config.forEachControllerPackage(controllerPackage -> {
      Set<Class<?>> controllers = ReflectionProviderHelper.getAnnotationProcessor(config, controllerPackage).getClassesTypesAnnotatedWith(SockJS.class);
      controllers.forEach(this::createSocketHandlers);
    });
  }

  private <T> void createSocketHandlers(Class<T> controller) {
    SockJSVisitor<T> visitor = new SockJSVisitor<>(controller, config, router);
    visitor.visit();
  }

}
