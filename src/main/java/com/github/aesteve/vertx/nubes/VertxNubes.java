package com.github.aesteve.vertx.nubes;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.cookies.CookieValue;
import com.github.aesteve.vertx.nubes.annotations.cookies.Cookies;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.mixins.Throttled;
import com.github.aesteve.vertx.nubes.annotations.routing.ClientRedirect;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PATCH;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PUT;
import com.github.aesteve.vertx.nubes.context.ClientAccesses;
import com.github.aesteve.vertx.nubes.context.PaginationContext;
import com.github.aesteve.vertx.nubes.context.RateLimit;
import com.github.aesteve.vertx.nubes.exceptions.MissingConfigurationException;
import com.github.aesteve.vertx.nubes.fixtures.FixtureLoader;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessorRegistry;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.handlers.impl.DefaultErrorHandler;
import com.github.aesteve.vertx.nubes.handlers.impl.LocaleHandler;
import com.github.aesteve.vertx.nubes.handlers.impl.PaginationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.PayloadTypeProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.RateLimitationHandler;
import com.github.aesteve.vertx.nubes.handlers.impl.UserProcessor;
import com.github.aesteve.vertx.nubes.i18n.LocaleResolver;
import com.github.aesteve.vertx.nubes.i18n.LocaleResolverRegistry;
import com.github.aesteve.vertx.nubes.i18n.impl.AcceptLanguageLocaleResolver;
import com.github.aesteve.vertx.nubes.marshallers.Payload;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.marshallers.impl.BoonPayloadMarshaller;
import com.github.aesteve.vertx.nubes.marshallers.impl.JAXBPayloadMarshaller;
import com.github.aesteve.vertx.nubes.reflections.RouteFactory;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapter;
import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapterRegistry;
import com.github.aesteve.vertx.nubes.reflections.adapters.impl.DefaultParameterAdapter;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.ClientRedirectProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.ContentTypeProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.FileProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.factories.impl.ViewProcessorFactory;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.impl.LocaleParamInjector;
import com.github.aesteve.vertx.nubes.services.ServiceRegistry;
import com.github.aesteve.vertx.nubes.utils.async.MultipleFutures;
import com.github.aesteve.vertx.nubes.views.TemplateEngineManager;

public class VertxNubes {

    private Vertx vertx;
    private Config config;
    private Router router;
    private FixtureLoader fixtureLoader;
    private ParameterAdapterRegistry registry;
    private AnnotationProcessorRegistry apRegistry;
    private TypedParamInjectorRegistry typeInjectors;
    private AnnotatedParamInjectorRegistry annotInjectors;
    private Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers;
    private Map<Class<?>, Processor> typeProcessors;
    private Map<String, PayloadMarshaller> marshallers;
    private Map<Class<?>, Handler<RoutingContext>> paramHandlers;
    private ServiceRegistry serviceRegistry;
    private Handler<RoutingContext> failureHandler;
    private TemplateEngineManager templManager;
    private LocaleResolverRegistry locResolvers;
    private AuthProvider authProvider;

    /**
     * TODO check config
     * 
     * @param vertx
     */
    public VertxNubes(Vertx vertx, JsonObject json) throws MissingConfigurationException {
        this.vertx = vertx;
        config = Config.fromJsonObject(json, vertx);
        registry = new ParameterAdapterRegistry(new DefaultParameterAdapter());
        annotationHandlers = new HashMap<Class<? extends Annotation>, Set<Handler<RoutingContext>>>();
        paramHandlers = new HashMap<Class<?>, Handler<RoutingContext>>();
        typeProcessors = new HashMap<Class<?>, Processor>();
        apRegistry = new AnnotationProcessorRegistry();
        marshallers = new HashMap<String, PayloadMarshaller>();
        typeInjectors = new TypedParamInjectorRegistry();
        annotInjectors = new AnnotatedParamInjectorRegistry(marshallers, registry);
        serviceRegistry = new ServiceRegistry(vertx);
        templManager = new TemplateEngineManager(config);
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
        registerAnnotationProcessor(ClientRedirect.class, new ClientRedirectProcessorFactory());
        registerAnnotationProcessor(ContentType.class, new ContentTypeProcessorFactory());
        registerAnnotationProcessor(View.class, new ViewProcessorFactory(templManager));
        registerAnnotationProcessor(File.class, new FileProcessorFactory());
        registerMarshaller("application/json", new BoonPayloadMarshaller());
        if (config.domainPackage != null) {
            try {
                registerMarshaller("application/xml", new JAXBPayloadMarshaller(config.domainPackage));
            } catch (JAXBException je) {
                throw new RuntimeException(je);
            }
        }
        failureHandler = new DefaultErrorHandler(config, templManager, marshallers);
    }

    public void bootstrap(Future<Router> future, Router paramRouter) {
        if (authProvider != null) {
            registerTypeProcessor(User.class, new UserProcessor(authProvider));
        }
        router = paramRouter;
        router.route().failureHandler(failureHandler);
        RouteFactory routeDiscovery = new RouteFactory(router, config, annotationHandlers, typeProcessors, apRegistry, typeInjectors, annotInjectors, serviceRegistry, paramHandlers);
        routeDiscovery.createRoutes();
        StaticHandler staticHandler;
        if (config.webroot != null) {
            staticHandler = StaticHandler.create(config.webroot);
        } else {
            staticHandler = StaticHandler.create();
        }
        router.route(config.assetsPath + "/*").handler(staticHandler);

        // fixtures
        fixtureLoader = new FixtureLoader(vertx, config, serviceRegistry);
        Future<Void> fixturesFuture = Future.future();
        fixturesFuture.setHandler(result -> {
            if (result.succeeded()) {
                periodicallyCleanHistoryMap();
                future.complete(router);
            } else {
                future.fail(result.cause());
            }
        });

        // services
        Future<Void> servicesFuture = Future.future();
        servicesFuture.setHandler(result -> {
            if (result.succeeded()) {
                fixtureLoader.setUp(fixturesFuture);
            } else {
                future.fail(result.cause());
            }
        });

        serviceRegistry.startAll(servicesFuture);
    }

    public void bootstrap(Future<Router> future) {
        bootstrap(future, Router.router(vertx));
    }

    public void stop(Future<Void> future) {
        router.clear();
        MultipleFutures futures = new MultipleFutures(future);
        futures.add(fixtureLoader::tearDown);
        futures.add(serviceRegistry::stopAll);
        futures.start();
    }

    public void setAvailableLocales(List<Locale> availableLocales) {
        if (locResolvers == null) {
            locResolvers = new LocaleResolverRegistry(availableLocales);
            locResolvers.addResolver(new AcceptLanguageLocaleResolver());
            registerTypeParamInjector(Locale.class, new LocaleParamInjector());
            registerHandler(Locale.class, new LocaleHandler(locResolvers));
        } else {
            locResolvers.addLocales(availableLocales);
        }
    }

    public void setDefaultLocale(Locale defaultLocale) {
        if (locResolvers == null) {
            locResolvers = new LocaleResolverRegistry(defaultLocale);
            locResolvers.addResolver(new AcceptLanguageLocaleResolver());
            registerTypeParamInjector(Locale.class, new LocaleParamInjector());
            registerHandler(Locale.class, new LocaleHandler(locResolvers));
        }
        locResolvers.setDefaultLocale(defaultLocale);
    }

    public void addLocaleResolver(LocaleResolver resolver) {
        if (locResolvers == null) {
            throw new IllegalArgumentException("Please set a list of available locales first. We can't guess the list of locales you're handling in your application.");
        }
        locResolvers.addResolver(resolver);
    }

    public void setFailureHandler(Handler<RoutingContext> handler) {
        failureHandler = handler;
    }

    public void registerService(Object service) {
        serviceRegistry.registerService(service);
    }

    public void registerHandler(Class<?> parameterClass, Handler<RoutingContext> handler) {
        paramHandlers.put(parameterClass, handler);
    }

    public <T> void registerAdapter(Class<T> parameterClass, ParameterAdapter<T> adapter) {
        registry.registerAdapter(parameterClass, adapter);
    }

    public void registerAnnotationHandler(Class<? extends Annotation> annotation, Handler<RoutingContext> handler) {
        Set<Handler<RoutingContext>> handlers = annotationHandlers.get(annotation);
        if (handlers == null) {
            handlers = new LinkedHashSet<Handler<RoutingContext>>();
        }
        handlers.add(handler);
        annotationHandlers.put(annotation, handlers);
    }

    public void registerTypeProcessor(Class<?> type, Processor processor) {
        typeProcessors.put(type, processor);
    }

    public <T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessorFactory<T> processor) {
        apRegistry.registerProcessor(annotation, processor);
    }

    public void registerMarshaller(String contentType, PayloadMarshaller marshaller) {
        marshallers.put(contentType, marshaller);
    }

    public <T> void registerTypeParamInjector(Class<? extends T> clazz, ParamInjector<T> injector) {
        typeInjectors.registerInjector(clazz, injector);
    }

    public <T extends Annotation> void registerAnnotatedParamInjector(Class<? extends T> clazz, AnnotatedParamInjector<T> injector) {
        annotInjectors.registerInjector(clazz, injector);
    }

    private void periodicallyCleanHistoryMap() {
        vertx.setPeriodic(60000, timerId -> {
            LocalMap<Object, Object> rateLimitations = vertx.sharedData().getLocalMap("mvc.rateLimitation");
            if (rateLimitations == null) {
                return;
            }
            List<String> clientIpsToRemove = new ArrayList<String>();
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
}
