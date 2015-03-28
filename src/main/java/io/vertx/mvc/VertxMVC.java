package io.vertx.mvc;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.apex.Router;
import io.vertx.mvc.context.ClientAccesses;
import io.vertx.mvc.context.RateLimit;
import io.vertx.mvc.reflections.RouteDiscovery;

import java.util.ArrayList;
import java.util.List;

public class VertxMVC {

    private Config config;
    private Vertx vertx;

    /**
     * TODO check config
     * 
     * @param vertx
     */
    public VertxMVC(Vertx vertx, JsonObject json) {
        this.vertx = vertx;
        config = Config.fromJsonObject(json);
    }

    public Router bootstrap(Router router) {
    	RouteDiscovery routeDiscovery = new RouteDiscovery(router, config);
    	routeDiscovery.createRoutes();
        periodicallyCleanHistoryMap();
        return router;
    }

    public Router bootstrap() {
        return bootstrap(Router.router(vertx));
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
