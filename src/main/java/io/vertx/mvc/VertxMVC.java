package io.vertx.mvc;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.BodyHandler;
import io.vertx.ext.apex.handler.CookieHandler;
import io.vertx.ext.apex.handler.StaticHandler;
import io.vertx.mvc.annotations.cookies.CookieValue;
import io.vertx.mvc.annotations.cookies.Cookies;
import io.vertx.mvc.annotations.mixins.ContentType;
import io.vertx.mvc.annotations.mixins.Throttled;
import io.vertx.mvc.annotations.params.RequestBody;
import io.vertx.mvc.annotations.routing.POST;
import io.vertx.mvc.annotations.routing.PUT;
import io.vertx.mvc.context.ClientAccesses;
import io.vertx.mvc.context.PaginationContext;
import io.vertx.mvc.context.RateLimit;
import io.vertx.mvc.exceptions.MissingConfigurationException;
import io.vertx.mvc.fixtures.FixtureLoader;
import io.vertx.mvc.handlers.AnnotationProcessor;
import io.vertx.mvc.handlers.AnnotationProcessorRegistry;
import io.vertx.mvc.handlers.Processor;
import io.vertx.mvc.handlers.impl.ContentTypeProcessor;
import io.vertx.mvc.handlers.impl.PaginationProcessor;
import io.vertx.mvc.handlers.impl.RateLimitationHandler;
import io.vertx.mvc.marshallers.PayloadMarshaller;
import io.vertx.mvc.marshallers.impl.BoonPayloadMarshaller;
import io.vertx.mvc.reflections.ParameterAdapter;
import io.vertx.mvc.reflections.ParameterAdapterRegistry;
import io.vertx.mvc.reflections.RouteDiscovery;
import io.vertx.mvc.reflections.impl.DefaultParameterAdapter;
import io.vertx.mvc.utils.SimpleFuture;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VertxMVC {

    private Config config;
    private Vertx vertx;
    private Router router;
    private FixtureLoader fixtureLoader;
    private ParameterAdapterRegistry registry;
    private AnnotationProcessorRegistry apRegistry;
    private Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers;
    private Map<Class<?>, Processor> typeProcessors;
    private Map<String, PayloadMarshaller> marshallers;
    
    /**
     * TODO check config
     * 
     * @param vertx
     */
    public VertxMVC(Vertx vertx, JsonObject json) throws MissingConfigurationException {
        this.vertx = vertx;
        config = Config.fromJsonObject(json);
        registry = new ParameterAdapterRegistry(new DefaultParameterAdapter());
        annotationHandlers = new HashMap<Class<? extends Annotation>, Set<Handler<RoutingContext>>>();
        typeProcessors = new HashMap<Class<?>, Processor>();
        apRegistry = new AnnotationProcessorRegistry();
        marshallers = new HashMap<String, PayloadMarshaller>();
        CookieHandler cookieHandler = CookieHandler.create();
        BodyHandler bodyHandler = BodyHandler.create();
        PaginationProcessor pageProcessor = new PaginationProcessor();
        registerAnnotationHandler(Cookies.class, cookieHandler);
        registerAnnotationHandler(CookieValue.class, cookieHandler);
        registerAnnotationHandler(Throttled.class, RateLimitationHandler.create(config));
        registerAnnotationHandler(POST.class, bodyHandler);
        registerAnnotationHandler(PUT.class, bodyHandler);
        registerAnnotationHandler(RequestBody.class, bodyHandler);
        registerAnnotationHandler(RequestBody.class, bodyHandler);
        registerTypeProcessor(PaginationContext.class, pageProcessor);
        registerAnnotationProcessor(ContentType.class, new ContentTypeProcessor());
        registerMarshaller("application/json", new BoonPayloadMarshaller());
    }

    public void bootstrap(Future<Router> future, Router paramRouter) {
    	router = paramRouter;
    	RouteDiscovery routeDiscovery = new RouteDiscovery(router, config, registry, annotationHandlers, typeProcessors, apRegistry, marshallers);
    	routeDiscovery.createRoutes();
    	StaticHandler staticHandler;
    	if (config.webroot != null) {
    		staticHandler = StaticHandler.create(config.webroot);
    	} else {
    		staticHandler = StaticHandler.create();
    	}
    	router.route(config.assetsPath+"/*").handler(staticHandler);
    	fixtureLoader = new FixtureLoader(vertx, config);
    	Future<Void> fixturesFuture = Future.future();
    	fixturesFuture.setHandler(handler -> {
    		if (handler.succeeded()) {
                periodicallyCleanHistoryMap();
                future.complete(router);
    		} else {
    			future.fail(handler.cause());
    		}
    	});
    	fixtureLoader.setUp(fixturesFuture);
    }

    public void bootstrap(Future<Router> future) {
        bootstrap(future, Router.router(vertx));
    }

    public Future<Void> stop() {
    	SimpleFuture<Void> future = new SimpleFuture<Void>();
    	router.clear();
    	fixtureLoader.tearDown(future);
    	return future;
    }
    
    public<T> void registerAdapter(Class<T> parameterClass, ParameterAdapter<T> adapter) {
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
    
    public<T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessor<T> processor) {
    	apRegistry.registerProcessor(annotation, processor);
    }
    
    public void registerMarshaller(String contentType, PayloadMarshaller marshaller) {
    	marshallers.put(contentType, marshaller);
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
