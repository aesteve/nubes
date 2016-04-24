package com.github.aesteve.vertx.nubes;

import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.auth.Logout;
import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.annotations.cookies.Cookies;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.mixins.Throttled;
import com.github.aesteve.vertx.nubes.annotations.routing.Redirect;
import com.github.aesteve.vertx.nubes.context.ClientAccesses;
import com.github.aesteve.vertx.nubes.context.PaginationContext;
import com.github.aesteve.vertx.nubes.context.RateLimit;
import com.github.aesteve.vertx.nubes.fixtures.FixtureLoader;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.handlers.impl.*;
import com.github.aesteve.vertx.nubes.i18n.LocaleResolver;
import com.github.aesteve.vertx.nubes.i18n.LocaleResolverRegistry;
import com.github.aesteve.vertx.nubes.i18n.impl.AcceptLanguageLocaleResolver;
import com.github.aesteve.vertx.nubes.marshallers.Payload;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.marshallers.impl.BoonPayloadMarshaller;
import com.github.aesteve.vertx.nubes.marshallers.impl.JAXBPayloadMarshaller;
import com.github.aesteve.vertx.nubes.marshallers.impl.PlainTextMarshaller;
import com.github.aesteve.vertx.nubes.reflections.AnnotVerticleFactory;
import com.github.aesteve.vertx.nubes.reflections.EventBusBridgeFactory;
import com.github.aesteve.vertx.nubes.reflections.RouteFactory;
import com.github.aesteve.vertx.nubes.reflections.SocketFactory;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapter;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.*;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl.LocaleParamInjector;
import com.github.aesteve.vertx.nubes.services.Service;
import com.github.aesteve.vertx.nubes.services.ServiceRegistry;
import com.github.aesteve.vertx.nubes.utils.async.AsyncUtils;
import com.github.aesteve.vertx.nubes.utils.async.MultipleFutures;
import com.github.aesteve.vertx.nubes.views.TemplateEngineManager;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.TemplateEngine;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;

import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.completeFinally;
import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.completeOrFail;

public class VertxNubes {

  private static final int CLEAN_HISTORY_DELAY = 60000;
  protected final Config config;
  protected final Vertx vertx;

  private Router router;
  private FixtureLoader fixtureLoader;
  private Handler<RoutingContext> failureHandler;
  private final ParameterAdapterRegistry registry;
  private final Map<String, PayloadMarshaller> marshallers;
  private LocaleResolverRegistry locResolver;
  private final List<String> deploymentIds;

  /**
   * @param vertx the vertx instance
   */
  public VertxNubes(Vertx vertx, JsonObject json) {
    this.vertx = vertx;
    config = Config.fromJsonObject(json, vertx);
    deploymentIds = new ArrayList<>();
    registry = new ParameterAdapterRegistry();
    marshallers = new HashMap<>();
    config.setMarshallers(marshallers);
    config.createAnnotInjectors(registry);

    // marshalling
    TemplateEngineManager templManager = new TemplateEngineManager(config);
    registerAnnotationProcessor(View.class, new ViewProcessorFactory(templManager));
    registerAnnotationProcessor(File.class, new FileProcessorFactory());
    registerMarshaller("text/plain", new PlainTextMarshaller());
    registerMarshaller("application/json", new BoonPayloadMarshaller());
    String domainPackage = config.getDomainPackage();
    if (domainPackage != null) {
      try {
        Reflections reflections = new Reflections(domainPackage, new SubTypesScanner(false));
        registerMarshaller("application/xml", new JAXBPayloadMarshaller(reflections.getSubTypesOf(Object.class)));
      } catch (JAXBException je) {
        throw new VertxException(je);
      }
    }
    failureHandler = new DefaultErrorHandler(config, templManager, marshallers);

    // default processors/handlers
    CookieHandler cookieHandler = CookieHandler.create();
    registerAnnotationHandler(Cookies.class, cookieHandler);
    registerAnnotationHandler(CookieValue.class, cookieHandler);
    registerAnnotationHandler(Throttled.class, RateLimitationHandler.create(config));
    registerTypeProcessor(PaginationContext.class, new PaginationProcessor());
    registerTypeProcessor(Payload.class, new PayloadTypeProcessor(marshallers));
    registerAnnotationProcessor(Redirect.class, new ClientRedirectProcessorFactory());
    registerAnnotationProcessor(ContentType.class, new ContentTypeProcessorFactory());
    registerAnnotationProcessor(Logout.class, new LogoutProcessor());
  }

  public void bootstrap(Handler<AsyncResult<Router>> handler, Router paramRouter) {
    setUpRouter(paramRouter);
    final ServiceRegistry serviceRegistry = config.getServiceRegistry();
    fixtureLoader = new FixtureLoader(vertx, config, serviceRegistry);
    Map<String, DeploymentOptions> verticles = new AnnotVerticleFactory(config).scan();
    MultipleFutures<String> vertFutures = new MultipleFutures<>(verticles, this::deployVerticle);
    AsyncUtils.chainOnSuccess(
        handler,
        vertFutures,
        serviceRegistry::startAll,
        fixtureLoader::setUp,
        res -> {
          vertx.setPeriodic(CLEAN_HISTORY_DELAY, this::cleanHistoryMap);
          handler.handle(Future.succeededFuture(router));
        });
    vertFutures.start();
  }

  public void bootstrap(Handler<AsyncResult<Router>> handler) {
    bootstrap(handler, Router.router(vertx));
  }

  public void stop(Handler<AsyncResult<Void>> handler) {
    router.clear();
    final ServiceRegistry serviceRegistry = config.getServiceRegistry();
    MultipleFutures<Void> futures = new MultipleFutures<>(handler);
    futures.add(fixtureLoader::tearDown);
    futures.add(serviceRegistry::stopAll);
    futures.add(this::stopDeployments);
    futures.start();
  }

  private void undeployVerticle(String deploymentId, Future<Void> future) {
    vertx.undeploy(deploymentId, completeFinally(future));
  }

  public void registerTemplateEngine(String extension, TemplateEngine engine) {
    config.registerTemplateEngine(extension, engine);
  }

  public void setAuthProvider(AuthProvider authProvider) {
    config.setAuthProvider(authProvider);
  }

  public void registerInterceptor(String name, Handler<RoutingContext> handler) {
    config.registerInterceptor(name, handler);
  }

  public void setAvailableLocales(List<Locale> availableLocales) {
    initLocale(availableLocales.toArray(new Locale[0]));
    locResolver.addLocales(availableLocales);
  }

  public void setDefaultLocale(Locale defaultLocale) {
    initLocale(defaultLocale);
    locResolver.setDefaultLocale(defaultLocale);
  }

  private void initLocale(Locale... loc) {
    if (locResolver == null) {
      locResolver = new LocaleResolverRegistry(Arrays.asList(loc));
      locResolver.addResolver(new AcceptLanguageLocaleResolver());
      registerTypeParamInjector(Locale.class, new LocaleParamInjector());
      addGlobalHandler(new LocaleHandler(locResolver));
    }
  }

  public void addLocaleResolver(LocaleResolver resolver) {
    if (locResolver == null) {
      throw new IllegalArgumentException("Please set a list of available locales first. We can't guess the list of locales you're handling in your application.");
    }
    locResolver.addResolver(resolver);
  }

  public void setFailureHandler(Handler<RoutingContext> handler) {
    failureHandler = handler;
  }

  public void registerService(String name, Object service) {
    config.registerService(name, service);
  }

  public Service getService(String name) {
    return (Service) config.getService(name);
  }

  public void registerServiceProxy(Object service) {
    config.registerService("$nubes-proxy$__" + service.getClass().getName(), service);
  }

  public void registerHandler(Class<?> parameterClass, Handler<RoutingContext> handler) {
    config.registerParamHandler(parameterClass, handler);
  }

  public <T> void registerAdapter(Class<T> parameterClass, ParameterAdapter<T> adapter) {
    registry.registerAdapter(parameterClass, adapter);
  }

  public void registerAnnotationHandler(Class<? extends Annotation> annotation, Handler<RoutingContext> handler) {
    Set<Handler<RoutingContext>> handlers = config.getAnnotationHandler(annotation);
    if (handlers == null) {
      handlers = new LinkedHashSet<>();
    }
    if (!handlers.contains(handler)) {
      handlers.add(handler);
    }
    config.registerAnnotationHandler(annotation, handlers);
  }

  public void registerTypeProcessor(Class<?> type, Processor processor) {
    config.registerTypeProcessor(type, processor);
  }

  public <T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessorFactory<T> processor) {
    config.registerAnnotationProcessor(annotation, processor);
  }

  public <T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessor<T> processor) {
    config.registerAnnotationProcessor(annotation, processor);
  }

  public void registerMarshaller(String contentType, PayloadMarshaller marshaller) {
    marshallers.put(contentType, marshaller);
  }

  public <T> void registerTypeParamInjector(Class<? extends T> clazz, ParamInjector<T> injector) {
    config.registerInjector(clazz, injector);
  }

  public <T extends Annotation> void registerAnnotatedParamInjector(Class<? extends T> clazz, AnnotatedParamInjector<T> injector) {
    config.registerInjector(clazz, injector);
  }

  public void addGlobalHandler(Handler<RoutingContext> handler) {
    config.addHandler(handler);
  }

  // private methods

  private void setUpRouter(Router paramRouter) {
    router = paramRouter;
    router.route().failureHandler(failureHandler);
    if (locResolver != null) {
      locResolver.getAvailableLocales().forEach(this::loadResourceBundle);
      if (locResolver.getDefaultLocale() != null) {
        loadResourceBundle(locResolver.getDefaultLocale());
      }
    }
    if (config.getAuthProvider() != null) {
      registerAnnotationProcessor(Auth.class, new AuthProcessorFactory());
    }
    new RouteFactory(router, config).createHandlers();
    new SocketFactory(router, config).createHandlers();
    new EventBusBridgeFactory(router, config).createHandlers();
    StaticHandler staticHandler;
    final String webroot = config.getWebroot();
    if (webroot != null) {
      staticHandler = StaticHandler.create(webroot);
    } else {
      staticHandler = StaticHandler.create();
    }
    router.route(config.getAssetsPath() + "/*").handler(staticHandler);
  }

  private void cleanHistoryMap(Long timerId) {
    LocalMap<String, ClientAccesses> rateLimitations = vertx.sharedData().getLocalMap("mvc.rateLimitation");
    if (rateLimitations == null) {
      return;
    }
    rateLimitations.keySet().stream()
        .filter(clientsWithNoAccessPredicate(rateLimitations))
        .forEach(rateLimitations::remove);
  }

  private Predicate<String> clientsWithNoAccessPredicate(LocalMap<String, ClientAccesses> rateLimitations) {
    RateLimit rateLimit = config.getRateLimit();
    return clientIp -> {
      ClientAccesses accesses = rateLimitations.get(clientIp);
      long keepAfter = rateLimit.getTimeUnit().toMillis(rateLimit.getValue());
      accesses.clearHistory(keepAfter);
      return accesses.noAccess();
    };
  }

  private void loadResourceBundle(Locale loc) {
    ResourceBundle bundle = ResourceBundle.getBundle(config.getI18nDir() + "messages", loc);
    config.createBundle(loc, bundle);
  }

  private void deployVerticle(String vertName, DeploymentOptions options, Future<String> future) {
    vertx.deployVerticle(vertName, options, completeOrFail(future));
  }

  private void stopDeployments(Future<Void> future) {
    MultipleFutures<Void> futures = new MultipleFutures<>(future);
    futures.addAll(deploymentIds, this::undeployVerticle);
    futures.start();
  }

}
