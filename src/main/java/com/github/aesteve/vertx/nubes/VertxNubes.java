package com.github.aesteve.vertx.nubes;

import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.completeFinally;
import static com.github.aesteve.vertx.nubes.utils.async.AsyncUtils.completeOrFail;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.annotations.cookies.Cookies;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.mixins.Throttled;
import com.github.aesteve.vertx.nubes.annotations.routing.Redirect;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PATCH;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PUT;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import com.github.aesteve.vertx.nubes.context.ClientAccesses;
import com.github.aesteve.vertx.nubes.context.PaginationContext;
import com.github.aesteve.vertx.nubes.context.RateLimit;
import com.github.aesteve.vertx.nubes.fixtures.FixtureLoader;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessorRegistry;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.handlers.impl.LocaleHandler;
import com.github.aesteve.vertx.nubes.handlers.impl.PaginationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.PayloadTypeProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.RateLimitationHandler;
import com.github.aesteve.vertx.nubes.i18n.LocaleResolver;
import com.github.aesteve.vertx.nubes.i18n.LocaleResolverRegistry;
import com.github.aesteve.vertx.nubes.i18n.impl.AcceptLanguageLocaleResolver;
import com.github.aesteve.vertx.nubes.marshallers.Payload;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.marshallers.impl.BoonPayloadMarshaller;
import com.github.aesteve.vertx.nubes.marshallers.impl.JAXBPayloadMarshaller;
import com.github.aesteve.vertx.nubes.reflections.AnnotVerticleFactory;
import com.github.aesteve.vertx.nubes.reflections.EventBusBridgeFactory;
import com.github.aesteve.vertx.nubes.reflections.RouteFactory;
import com.github.aesteve.vertx.nubes.reflections.SocketFactory;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapter;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.adapters.impl.DefaultParameterAdapter;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.AuthProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.ClientRedirectProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.ContentTypeProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.FileProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.ViewProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl.LocaleParamInjector;
import com.github.aesteve.vertx.nubes.utils.async.MultipleFutures;
import com.github.aesteve.vertx.nubes.views.TemplateEngineManager;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.TemplateEngine;

public class VertxNubes {

	private Vertx vertx;
	private Config config;
	private Router router;
	private FixtureLoader fixtureLoader;
	private Handler<RoutingContext> failureHandler;
	private ParameterAdapterRegistry registry;
	private Map<String, PayloadMarshaller> marshallers;
	private LocaleResolverRegistry locResolver;
	private List<String> deploymentIds;

	/**
	 * TODO check config
	 * 
	 * @param vertx
	 */
	public VertxNubes(Vertx vertx, JsonObject json) {
		this.vertx = vertx;
		config = Config.fromJsonObject(json, vertx);
		deploymentIds = new ArrayList<>();
		registry = new ParameterAdapterRegistry(new DefaultParameterAdapter());
		config.annotationHandlers = new HashMap<>();
		config.paramHandlers = new HashMap<>();
		config.typeProcessors = new HashMap<>();
		config.apRegistry = new AnnotationProcessorRegistry();
		marshallers = new HashMap<>();
		config.typeInjectors = new TypedParamInjectorRegistry(config);
		config.annotInjectors = new AnnotatedParamInjectorRegistry(marshallers, registry);
		config.aopHandlerRegistry = new HashMap<>();
		config.marshallers = marshallers;
		CookieHandler cookieHandler = CookieHandler.create();
		BodyHandler bodyHandler = BodyHandler.create();
		registerAnnotationHandler(Cookies.class, cookieHandler);
		registerAnnotationHandler(CookieValue.class, cookieHandler);
		registerAnnotationHandler(Throttled.class, RateLimitationHandler.create(config));
		registerAnnotationHandler(GET.class, bodyHandler);
		registerAnnotationHandler(POST.class, bodyHandler);
		registerAnnotationHandler(PUT.class, bodyHandler);
		registerAnnotationHandler(PATCH.class, bodyHandler);
		registerTypeProcessor(PaginationContext.class, new PaginationProcessor());
		registerTypeProcessor(Payload.class, new PayloadTypeProcessor(marshallers));
		registerAnnotationProcessor(Redirect.class, new ClientRedirectProcessorFactory());
		registerAnnotationProcessor(ContentType.class, new ContentTypeProcessorFactory());
		TemplateEngineManager templManager = new TemplateEngineManager(config);
		registerAnnotationProcessor(View.class, new ViewProcessorFactory(templManager));
		registerAnnotationProcessor(File.class, new FileProcessorFactory());
		registerMarshaller("application/json", new BoonPayloadMarshaller());
		if (config.domainPackage != null) {
			try {
				Reflections reflections = new Reflections(config.domainPackage, new SubTypesScanner(false));
				registerMarshaller("application/xml", new JAXBPayloadMarshaller(reflections.getSubTypesOf(Object.class)));
			} catch (JAXBException je) {
				throw new RuntimeException(je);
			}
		}
		failureHandler = new DefaultErrorHandler(config, templManager, marshallers);
	}

	public void bootstrap(Handler<AsyncResult<Router>> handler, Router paramRouter) {
		setUpRouter(paramRouter);

		// fixtures
		fixtureLoader = new FixtureLoader(vertx, config, config.serviceRegistry);
		Future<Void> fixturesFuture = Future.future();
		fixturesFuture.setHandler(result -> {
			if (result.succeeded()) {
				periodicallyCleanHistoryMap();
				handler.handle(Future.succeededFuture(router));
			} else {
				handler.handle(Future.failedFuture(result.cause()));
			}
		});

		// services
		Future<Void> servicesFuture = Future.future();
		servicesFuture.setHandler(result -> {
			if (result.succeeded()) {
				fixtureLoader.setUp(fixturesFuture);
			} else {
				handler.handle(Future.failedFuture(result.cause()));
			}
		});

		// verticles
		MultipleFutures<String> vertFutures = new MultipleFutures<>();
		AnnotVerticleFactory vertFactory = new AnnotVerticleFactory(config);
		Map<String, DeploymentOptions> verticles = vertFactory.scan();
		vertFutures.setHandler(res -> {
			if (res.succeeded()) {
				config.serviceRegistry.startAll(servicesFuture);
			} else {
				handler.handle(Future.failedFuture(res.cause()));
			}
		});
		verticles.forEach((vertName, options) -> {
			vertFutures.add(fut -> {
				deployVerticle(vertName, options, fut);
			});
		});
		vertFutures.start();
	}

	private void deployVerticle(String vertName, DeploymentOptions options, Future<String> future) {
		vertx.deployVerticle(vertName, options, completeOrFail(future));
	}

	private void setUpRouter(Router paramRouter) {
		router = paramRouter;
		router.route().failureHandler(failureHandler);
		if (locResolver != null) {
			for (Locale loc : locResolver.getAvailableLocales()) {
				loadResourceBundle(loc);
			}
			loadResourceBundle(locResolver.getDefaultLocale());
		}
		if (config.authProvider != null) {
			registerAnnotationProcessor(Auth.class, new AuthProcessorFactory());
		}
		RouteFactory routeDiscovery = new RouteFactory(router, config);
		routeDiscovery.createHandlers();
		SocketFactory socketFactory = new SocketFactory(router, config);
		socketFactory.createHandlers();
		EventBusBridgeFactory ebBridgeFactory = new EventBusBridgeFactory(router, config);
		ebBridgeFactory.createHandlers();
		StaticHandler staticHandler;
		if (config.webroot != null) {
			staticHandler = StaticHandler.create(config.webroot);
		} else {
			staticHandler = StaticHandler.create();
		}
		router.route(config.assetsPath + "/*").handler(staticHandler);
	}

	public void bootstrap(Handler<AsyncResult<Router>> handler) {
		bootstrap(handler, Router.router(vertx));
	}

	public void stop(Handler<AsyncResult<Void>> handler) {
		router.clear();
		MultipleFutures<Void> futures = new MultipleFutures<>(handler);
		futures.add(fixtureLoader::tearDown);
		futures.add(config.serviceRegistry::stopAll);
		futures.add(this::stopDeployments);
		futures.start();
	}

	private void stopDeployments(Future<Void> future) {
		MultipleFutures<Void> futures = new MultipleFutures<>(future);
		deploymentIds.forEach(deploymentId -> {
			futures.add(fut -> {
				undeployVerticle(deploymentId, fut);
			});
		});
		futures.start();
	}

	private void undeployVerticle(String deploymentId, Future<Void> future) {
		vertx.undeploy(deploymentId, completeFinally(future));
	}

	public void registerTemplateEngine(String extension, TemplateEngine engine) {
		config.templateEngines.put(extension, engine);
	}

	public void setAuthProvider(AuthProvider authProvider) {
		config.authProvider = authProvider;
	}

	public void setAuthMethod(AuthMethod authMethod) {
		config.authMethod = authMethod;
	}

	public void registerInterceptor(String name, Handler<RoutingContext> handler) {
		config.aopHandlerRegistry.put(name, handler);
	}

	public void setAvailableLocales(List<Locale> availableLocales) {
		if (locResolver == null) {
			locResolver = new LocaleResolverRegistry(availableLocales);
			locResolver.addResolver(new AcceptLanguageLocaleResolver());
			registerTypeParamInjector(Locale.class, new LocaleParamInjector());
			addGlobalHandler(new LocaleHandler(locResolver));
		} else {
			locResolver.addLocales(availableLocales);
		}
	}

	public void setDefaultLocale(Locale defaultLocale) {
		if (locResolver == null) {
			locResolver = new LocaleResolverRegistry(defaultLocale);
			locResolver.addResolver(new AcceptLanguageLocaleResolver());
			registerTypeParamInjector(Locale.class, new LocaleParamInjector());
			addGlobalHandler(new LocaleHandler(locResolver));
		}
		locResolver.setDefaultLocale(defaultLocale);
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
		config.serviceRegistry.registerService(name, service);
	}

	public void registerServiceProxy(Object service) {
		config.serviceRegistry.registerService("$nubes-proxy$__" + service.getClass().getName(), service);
	}

	public void registerHandler(Class<?> parameterClass, Handler<RoutingContext> handler) {
		config.paramHandlers.put(parameterClass, handler);
	}

	public <T> void registerAdapter(Class<T> parameterClass, ParameterAdapter<T> adapter) {
		registry.registerAdapter(parameterClass, adapter);
	}

	public void registerAnnotationHandler(Class<? extends Annotation> annotation, Handler<RoutingContext> handler) {
		Set<Handler<RoutingContext>> handlers = config.annotationHandlers.get(annotation);
		if (handlers == null) {
			handlers = new LinkedHashSet<Handler<RoutingContext>>();
		}
		if (!handlers.contains(handler)) {
			handlers.add(handler);
		}
		config.annotationHandlers.put(annotation, handlers);
	}

	public void registerTypeProcessor(Class<?> type, Processor processor) {
		config.typeProcessors.put(type, processor);
	}

	public <T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessorFactory<T> processor) {
		config.apRegistry.registerProcessor(annotation, processor);
	}

	public void registerMarshaller(String contentType, PayloadMarshaller marshaller) {
		marshallers.put(contentType, marshaller);
	}

	public <T> void registerTypeParamInjector(Class<? extends T> clazz, ParamInjector<T> injector) {
		config.typeInjectors.registerInjector(clazz, injector);
	}

	public <T extends Annotation> void registerAnnotatedParamInjector(Class<? extends T> clazz, AnnotatedParamInjector<T> injector) {
		config.annotInjectors.registerInjector(clazz, injector);
	}

	public void addGlobalHandler(Handler<RoutingContext> handler) {
		config.globalHandlers.add(handler);
	}

	private void periodicallyCleanHistoryMap() {
		vertx.setPeriodic(60000, timerId -> {
			LocalMap<Object, Object> rateLimitations = vertx.sharedData().getLocalMap("mvc.rateLimitation");
			if (rateLimitations == null) {
				return;
			}
			List<String> clientIpsToRemove = new ArrayList<>();
			RateLimit rateLimit = config.rateLimit;
			for (Object key : rateLimitations.keySet()) {
				String clientIp = (String) key;
				ClientAccesses accesses = (ClientAccesses) rateLimitations.get(clientIp);
				long keepAfter = config.rateLimit.getTimeUnit().toMillis(rateLimit.getValue());
				accesses.clearHistory(keepAfter);
				if (accesses.noAccess()) {
					clientIpsToRemove.add(clientIp);
				}
			}
			clientIpsToRemove.forEach(clientIp -> {
				rateLimitations.remove(clientIp);
			});
		});
	}

	private void loadResourceBundle(Locale loc) {
		ResourceBundle bundle = ResourceBundle.getBundle(config.i18nDir + "messages", loc);
		config.bundlesByLocale.put(loc, bundle);
	}
}
