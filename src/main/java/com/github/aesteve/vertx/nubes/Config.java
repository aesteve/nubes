
package com.github.aesteve.vertx.nubes;

import com.github.aesteve.vertx.nubes.context.RateLimit;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessorRegistry;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.annotations.IAnnotationProvider;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.services.ServiceRegistry;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.shiro.ShiroAuth;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.templ.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Config {

  private static final Logger LOG = LoggerFactory.getLogger(Config.class);
  private final Map<Locale, ResourceBundle> bundlesByLocale;
  private final List<Handler<RoutingContext>> globalHandlers;
  private final Map<String, TemplateEngine> templateEngines;
  private final SockJSHandlerOptions sockJSOptions;
  private JsonObject json;
  private List<String> controllerPackages;
  private List<String> fixturePackages;
  private String reflectionProvider;
  private String verticlePackage;
  private String domainPackage;
  private RateLimit rateLimit;
  private String webroot;
  private String assetsPath;
  private String tplDir;
  private boolean displayErrors;
  private Vertx vertx;
  private AuthProvider authProvider;
  private String i18nDir;
  private AnnotationProcessorRegistry apRegistry;
  private Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers;
  private Map<Class<?>, Processor> typeProcessors;
  private TypedParamInjectorRegistry typeInjectors;
  private AnnotatedParamInjectorRegistry annotInjectors;
  private ServiceRegistry serviceRegistry;
  private Map<Class<?>, Handler<RoutingContext>> paramHandlers;
  private Map<String, Handler<RoutingContext>> aopHandlerRegistry;
  private Map<String, PayloadMarshaller> marshallers;

  private Config() {
    bundlesByLocale = new HashMap<>();
    globalHandlers = new ArrayList<>();
    templateEngines = new HashMap<>();
    sockJSOptions = new SockJSHandlerOptions();
    marshallers = new HashMap<>();
    annotationHandlers = new HashMap<>();
    paramHandlers = new HashMap<>();
    typeProcessors = new HashMap<>();
    apRegistry = new AnnotationProcessorRegistry();
    typeInjectors = new TypedParamInjectorRegistry(this);
    aopHandlerRegistry = new HashMap<>();
  }

  /**
   * TODO : we should be consistent on single/multiple values
   * (controllers is an array, fixtures is a list, domain is a single value, verticle is a single value) : this is wrong
   *
   * @param json JsonObject describing the config
   * @return config a type safe config object
   */
  public static Config fromJsonObject(JsonObject json, Vertx vertx) {
    Config instance = new Config();
    instance.json = json;
    instance.vertx = vertx;

    instance.readPackages();
    // Register services included in config
    instance.createServices();
    // Register templateEngines for extensions added in config
    instance.createTemplateEngines();

    instance.createRateLimit();

    instance.createAuthHandlers();

    instance.webroot = json.getString("webroot", "web/assets");
    instance.assetsPath = json.getString("static-path", "/assets");
    instance.tplDir = json.getString("views-dir", "web/views");
    instance.displayErrors = json.getBoolean("display-errors", Boolean.FALSE);
    // TODO : read sockJSOptions from config

    instance.reflectionProvider = json.getString("relectionprovider", "reflections");
    instance.globalHandlers.add(BodyHandler.create());
    return instance;
  }

  private void createAuthHandlers() {
    String auth = json.getString("auth-type");
    JsonObject authProperties = json.getJsonObject("auth-properties");

    // TODO : discuss it. I'm really not convinced about all the boilerplate needed in config (dbName only for JDBC, etc.)
    if (authProperties != null) {
      // For now, only JWT,Shiro and JDBC supported (same as for Vert.x web)
      switch (auth) {
        case "JWT":// For now only allow properties realm
          this.authProvider = JWTAuth.create(vertx, authProperties);
          break;
        case "Shiro":
          ShiroAuth.create(vertx, new ShiroAuthOptions(authProperties));
          break;
        case "JDBC":
          String dbName = json.getString("db-name");
          Objects.requireNonNull(dbName);
          JDBCClient client = JDBCClient.createShared(vertx, authProperties, dbName);
          this.authProvider = JDBCAuth.create(vertx, client);
          break;
        default:
          LOG.warn("Unknown type of auth : " + auth + " . Ignoring.");
      }
    } else if (auth != null) {
      LOG.warn("You have defined " + auth + " as auth type, but didn't provide any configuration, can't create authProvider");
    }

  }

  private void createRateLimit() {
    JsonObject rateLimitJson = json.getJsonObject("throttling");
    if (rateLimitJson != null) {
      int count = rateLimitJson.getInteger("count");
      int value = rateLimitJson.getInteger("time-frame");
      TimeUnit timeUnit = TimeUnit.valueOf(rateLimitJson.getString("time-unit"));
      this.rateLimit = new RateLimit(count, value, timeUnit);
    }
  }

  private void createTemplateEngines() {
    JsonArray templates = json.getJsonArray("templates", new JsonArray());
    if (templates.contains("hbs")) {
      this.templateEngines.put("hbs", HandlebarsTemplateEngine.create());
    }
    if (templates.contains("jade")) {
      this.templateEngines.put("jade", JadeTemplateEngine.create());
    }
    if (templates.contains("templ")) {
      this.templateEngines.put("templ", MVELTemplateEngine.create());
    }
    if (templates.contains("thymeleaf")) {
      this.templateEngines.put("html", ThymeleafTemplateEngine.create());
    }
  }

  private void createServices() {
    JsonObject services = json.getJsonObject("services", new JsonObject());
    this.serviceRegistry = new ServiceRegistry(vertx, this);
    services.forEach(entry -> {
      String name = entry.getKey();
      String className = (String) entry.getValue();
      try {
        Class<?> clazz = Class.forName(className);
        this.serviceRegistry.registerService(name, clazz.newInstance());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        throw new VertxException(e);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private void readPackages() {
    this.i18nDir = json.getString("i18nDir", "web/i18n/");
    if (!this.i18nDir.endsWith("/")) {
      this.i18nDir = this.i18nDir + "/";
    }
    String srcPackage = json.getString("src-package");
    JsonArray controllers = json.getJsonArray("controller-packages");
    if (controllers == null) {
      controllers = new JsonArray();
      if (srcPackage != null) {
        controllers.add(srcPackage + ".controllers");
      }
    }
    this.controllerPackages = controllers.getList();

    this.verticlePackage = json.getString("verticle-package");
    if (this.verticlePackage == null && srcPackage != null) {
      this.verticlePackage = srcPackage + ".verticles";
    }

    this.domainPackage = json.getString("domain-package");
    if (this.domainPackage == null && srcPackage != null) {
      this.domainPackage = srcPackage + ".domains";
    }
    JsonArray fixtures = json.getJsonArray("fixture-packages");
    if (fixtures == null) {
      fixtures = new JsonArray();
      if (srcPackage != null) {
        fixtures.add(srcPackage + ".fixtures");
      }
    }
    this.fixturePackages = fixtures.getList();
  }

  public ResourceBundle getResourceBundle(Locale loc) {
    return bundlesByLocale.get(loc);
  }

  public JsonObject json() {
    return json;
  }

  public List<String> getFixturePackages() {
    return fixturePackages;
  }

  public TypedParamInjectorRegistry getTypeInjectors() {
    return typeInjectors;
  }

  public AnnotatedParamInjectorRegistry getAnnotatedInjectors() {
    return annotInjectors;
  }

  public boolean isDisplayErrors() {
    return displayErrors;
  }

  public Map<String, TemplateEngine> getTemplateEngines() {
    return templateEngines;
  }

  public String getTplDir() {
    return tplDir;
  }

  public RateLimit getRateLimit() {
    return rateLimit;
  }

  void createAnnotInjectors(ParameterAdapterRegistry registry) {
    annotInjectors = new AnnotatedParamInjectorRegistry(marshallers, registry);
  }

  public String getReflectionProvider() {
    return reflectionProvider;
  }

  public String getDomainPackage() {
    return domainPackage;
  }

  public ServiceRegistry getServiceRegistry() {
    return serviceRegistry;
  }

  void registerTemplateEngine(String extension, TemplateEngine engine) {
    templateEngines.put(extension, engine);
  }

  void registerInterceptor(String name, Handler<RoutingContext> handler) {
    aopHandlerRegistry.put(name, handler);
  }

  void registerService(String name, Object service) {
    serviceRegistry.registerService(name, service);
  }

  Object getService(String name) {
    return serviceRegistry.get(name);
  }

  void registerParamHandler(Class<?> parameterClass, Handler<RoutingContext> handler) {
    paramHandlers.put(parameterClass, handler);
  }

  public Set<Handler<RoutingContext>> getAnnotationHandler(Class<? extends Annotation> annotation) {
    return annotationHandlers.get(annotation);
  }

  void registerAnnotationHandler(Class<? extends Annotation> annotation, Set<Handler<RoutingContext>> handlers) {
    annotationHandlers.put(annotation, handlers);
  }

  void registerTypeProcessor(Class<?> type, Processor processor) {
    typeProcessors.put(type, processor);
  }

  <T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessorFactory<T> processor) {
    apRegistry.registerProcessor(annotation, processor);
  }

  <T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessor<T> processor) {
    apRegistry.registerProcessor(annotation, processor);
  }

  <T> void registerInjector(Class<? extends T> clazz, ParamInjector<T> injector) {
    typeInjectors.registerInjector(clazz, injector);
  }

  <T extends Annotation> void registerInjector(Class<? extends T> clazz, AnnotatedParamInjector<T> injector) {
    annotInjectors.registerInjector(clazz, injector);
  }

  void addHandler(Handler<RoutingContext> handler) {
    globalHandlers.add(handler);
  }

  String getWebroot() {
    return webroot;
  }

  String getAssetsPath() {
    return assetsPath;
  }

  public AuthProvider getAuthProvider() {
    return authProvider;
  }

  void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }

  String getI18nDir() {
    return i18nDir;
  }

  void createBundle(Locale loc, ResourceBundle bundle) {
    bundlesByLocale.put(loc, bundle);
  }

  public String getVerticlePackage() {
    return verticlePackage;
  }

  public void forEachControllerPackage(Handler<? super String> consumer) {
    controllerPackages.forEach(consumer::handle);
  }

  public Vertx getVertx() {
    return vertx;
  }

  public SockJSHandlerOptions getSockJSOptions() {
    return sockJSOptions;
  }

  public Processor getTypeProcessor(Class<?> parameterClass) {
    return typeProcessors.get(parameterClass);
  }

  public Handler<RoutingContext> getParamHandler(Class<?> parameterClass) {
    return paramHandlers.get(parameterClass);
  }

  public<T extends Annotation> AnnotationProcessor<T> getAnnotationProcessor(T methodAnnotation) {
    return apRegistry.getProcessor(methodAnnotation);
  }

  public Handler<RoutingContext> getAopHandler(String name) {
    return aopHandlerRegistry.get(name);
  }

  public void forEachGlobalHandler(Handler<Handler<RoutingContext>> handler) {
    globalHandlers.forEach(handler::handle);
  }

  public Map<String, PayloadMarshaller> getMarshallers() {
    return marshallers;
  }

  public void setMarshallers(Map<String, PayloadMarshaller> marshallers) {
    this.marshallers = marshallers;
  }

  public List<String> getControllerPackages() {
    return controllerPackages;
  }

}
