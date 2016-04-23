package com.github.aesteve.vertx.nubes.reflections.injectors.typed;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.context.PaginationContext;
import com.github.aesteve.vertx.nubes.marshallers.Payload;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl.*;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TypedParamInjectorRegistry {

  private final Map<Class<?>, ParamInjector<?>> map;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public TypedParamInjectorRegistry(Config config) {
    map = new HashMap<>();
    registerInjector(Vertx.class, new VertxParamInjector());
    registerInjector(Session.class, new SessionParamInjector());
    registerInjector(RoutingContext.class, new RoutingContextParamInjector());
    registerInjector(Payload.class, new PayloadParamInjector());
    registerInjector(PaginationContext.class, new PaginationContextParamInjector());
    registerInjector(EventBus.class, new EventBusParamInjector());
    registerInjector(ResourceBundle.class, new ResourceBundleParamInjector(config));
    registerInjector(HttpServerRequest.class, new RequestParamInjector());
    registerInjector(HttpServerResponse.class, new ResponseParamInjector());
    registerInjector(SocketAddress.class, new SocketAddressParamInjector());
    registerInjector(HttpVersion.class, new HttpVersionParamInjector());
  }

  public <T> void registerInjector(Class<? extends T> clazz, ParamInjector<T> injector) {
    map.put(clazz, injector);
  }

  @SuppressWarnings("unchecked")
  public <T> ParamInjector<T> getInjector(Class<? extends T> clazz) {

    return (ParamInjector<T>) map.get(clazz);
  }
}
