package io.vertx.mvc;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.handler.StaticHandler;
import io.vertx.mvc.context.ClientAccesses;
import io.vertx.mvc.context.RateLimit;
import io.vertx.mvc.exceptions.MissingConfigurationException;
import io.vertx.mvc.fixtures.FixtureLoader;
import io.vertx.mvc.reflections.RouteDiscovery;
import io.vertx.mvc.utils.SimpleFuture;

import java.util.ArrayList;
import java.util.List;

public class VertxMVC {

    private Config config;
    private Vertx vertx;
    private Router router;
    private FixtureLoader fixtureLoader;

    /**
     * TODO check config
     * 
     * @param vertx
     */
    public VertxMVC(Vertx vertx, JsonObject json) throws MissingConfigurationException {
        this.vertx = vertx;
        config = Config.fromJsonObject(json);
    }

    public void bootstrap(Future<Router> future, Router paramRouter) {
    	router = paramRouter;
    	RouteDiscovery routeDiscovery = new RouteDiscovery(router, config);
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
