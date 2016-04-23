package com.github.aesteve.vertx.nubes.reflections.visitors;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.EventBusBridge;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.InboundPermitted;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.OutboundPermitted;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventBusBridgeVisitor<T> extends BaseVisitor<T> {

  private final static Logger LOG = LoggerFactory.getLogger(EventBusBridgeVisitor.class);

  private SockJSHandler sockJSHandler;

  public EventBusBridgeVisitor(Class<T> clazz, Config config, Router router) {
    super(clazz, config, router);
  }

  public void visit() {
    sockJSHandler = SockJSHandler.create(config.getVertx(), config.getSockJSOptions());
    try {
      instance = clazz.newInstance();
      injectServices();
    } catch (Exception e) {
      throw new VertxException("Could not instanciate socket controller : " + clazz.getName(), e);
    }
    EventBusBridge annot = clazz.getAnnotation(EventBusBridge.class);
    path = annot.value();
    BridgeOptions bridge = createBridgeOptions(clazz);
    Map<BridgeEventType, Method> handlers = BridgeEventFactory.createFromController(clazz);
    sockJSHandler.bridge(bridge, be -> {
      Method method = handlers.get(be.type());
      if (method != null) {
        tryToInvoke(instance, method, be);
      } else {
        be.complete(true);
      }
    });
    normalizePath();
    router.route(path).handler(sockJSHandler);
  }

  private static BridgeOptions createBridgeOptions(Class<?> controller) {
    BridgeOptions options = new BridgeOptions();
    InboundPermitted[] inbounds = controller.getAnnotationsByType(InboundPermitted.class);
    if (inbounds.length > 0) {
      List<PermittedOptions> inboundPermitteds = new ArrayList<>(inbounds.length);
      for (InboundPermitted inbound : inbounds) {
        inboundPermitteds.add(createInboundPermitted(inbound));
      }
      options.setInboundPermitted(inboundPermitteds);

    } else {
      options.addInboundPermitted(new PermittedOptions());
    }
    OutboundPermitted[] outbounds = controller.getAnnotationsByType(OutboundPermitted.class);
    if (outbounds.length > 0) {
      List<PermittedOptions> outboundPermitteds = new ArrayList<>(outbounds.length);
      for (OutboundPermitted outbound : outbounds) {
        outboundPermitteds.add(createOutboundPermitted(outbound));
      }
      options.setOutboundPermitted(outboundPermitteds);
    } else {
      options.addOutboundPermitted(new PermittedOptions());
    }
    return options;
  }

  private static PermittedOptions createOutboundPermitted(OutboundPermitted outbound) {
    String address = outbound.address();
    String addressRegex = outbound.addressRegex();
    String requiredAuthority = outbound.requiredAuthority();
    return createPermittedOptions(address, addressRegex, requiredAuthority);
  }

  private static PermittedOptions createInboundPermitted(InboundPermitted inbound) {
    String address = inbound.address();
    String addressRegex = inbound.addressRegex();
    String requiredAuthority = inbound.requiredAuthority();
    return createPermittedOptions(address, addressRegex, requiredAuthority);
  }

  private static PermittedOptions createPermittedOptions(String address, String addressRegex, String requiredAuthority) {
    PermittedOptions options = new PermittedOptions();
    if (!"".equals(address)) {
      options.setAddress(address);
    }
    if (!"".equals(addressRegex)) {
      options.setAddressRegex(addressRegex);
    }
    if (!"".equals(requiredAuthority)) {
      options.setRequiredAuthority(requiredAuthority);
    }
    return options;
  }

  private void tryToInvoke(Object instance, Method method, BridgeEvent be) {
    List<Object> paramInstances = new ArrayList<>();
    for (Class<?> parameterClass : method.getParameterTypes()) {
      final Vertx vertx = config.getVertx();
      if (parameterClass.equals(BridgeEvent.class)) {
        paramInstances.add(be);
      } else if (parameterClass.equals(EventBus.class)) {
        paramInstances.add(vertx.eventBus());
      } else if (parameterClass.equals(Vertx.class)) {
        paramInstances.add(vertx);
      }
    }
    try {
      method.invoke(instance, paramInstances.toArray());
    } catch (Exception e) {
      LOG.error("Error while handling websocket", e);
      if (!be.failed() && !be.succeeded()) {
        be.fail(e);
      }
    }
  }


}
