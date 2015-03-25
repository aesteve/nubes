package io.vertx.mvc.routing;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.apex.Router;
import io.vertx.mvc.context.ClientAccesses;
import io.vertx.mvc.context.PaginationContext;
import io.vertx.mvc.context.RateLimit;
import io.vertx.mvc.controllers.ApiController;
import io.vertx.mvc.exceptions.HttpException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MVCRoute {

    private final static Logger logger = Logger.getLogger("");

    private final String path;
    private final HttpMethod httpMethod;
    private final Object instance;
    private List<Method> beforeFilters;
    private List<Method> afterFilters;
    private Method mainHandler;
    private Method finalizer;
    private boolean paginated;
    private RateLimit rateLimit;

    public MVCRoute(Object instance, String path, boolean paginated) {
        this(instance, path, HttpMethod.GET, paginated);
    }

    public MVCRoute(Object instance, String path, HttpMethod method, boolean paginated) {
        this.instance = instance;
        this.path = path;
        this.httpMethod = method;
        this.beforeFilters = new ArrayList<Method>();
        this.afterFilters = new ArrayList<Method>();
        this.paginated = paginated;
    }

    public void setMainHandler(Method mainHandler) {
        this.mainHandler = mainHandler;
    }

    public void setFinalizer(Method finalizer) {
        this.finalizer = finalizer;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public boolean isRateLimited() {
        return rateLimit != null;
    }

    public void addBeforeFilters(List<Method> beforeFilters) {
        this.beforeFilters.addAll(beforeFilters);
    }

    public void addAfterFilters(List<Method> afterFilters) {
        this.afterFilters.addAll(afterFilters);
    }

    public String path() {
        return path;
    }

    public HttpMethod method() {
        return httpMethod;
    }

    public void attachHandlersToRouter(Router router) {
        if (isRateLimited()) {
            attachLimitationHandler(router);
        }
        if (paginated) {
            readPaginationFromContext(router);
        }
        beforeFilters.forEach(filter -> {
            setHandler(router, filter);
        });
        setHandler(router, mainHandler);
        afterFilters.forEach(filter -> {
            setHandler(router, filter);
        });
        if (paginated && instance instanceof ApiController) {
            setPaginationOnResponse(router);
        }
        if (finalizer != null) {
            setHandler(router, finalizer);
        }
    }

    private void setHandler(Router router, Method method) {
        router.route(httpMethod, path).handler(routingContext -> {
            if (routingContext.response().ended()) {
                return;
            }
            try {
                method.invoke(instance, routingContext);
            } catch (Exception e) {
                e.printStackTrace();
                routingContext.fail(e);
            }
        });
    }

    private void attachLimitationHandler(Router router) {
        router.route(httpMethod, path).handler(context -> {
            Vertx vertx = context.vertx();
            LocalMap<Object, Object> rateLimitations = vertx.sharedData().getLocalMap("mvc.rateLimitation");
            String clientIp = context.request().remoteAddress().host();
            JsonObject json = (JsonObject) rateLimitations.get(clientIp);
            ClientAccesses accesses;
            if (json == null) {
                accesses = new ClientAccesses();
            } else {
                accesses = ClientAccesses.fromJsonObject(json);
            }
            accesses.newAccess();
            rateLimitations.put(clientIp, accesses.toJsonObject());
            if (accesses.isOverLimit(rateLimit)) {
                context.fail(420);
            } else {
                context.next();
            }
        });
    }

    private void readPaginationFromContext(Router router) {
        router.route(httpMethod, path).handler(context -> {
            try {
                context.data().put(PaginationContext.DATA_ATTR, PaginationContext.fromContext(context));
                context.next();
            } catch (HttpException he) {
                context.response().setStatusCode(he.getStatusCode());
                context.response().end();
            }
        });
    }

    private void setPaginationOnResponse(Router router) {
        router.route(httpMethod, path).handler(context -> {
            PaginationContext pageContext = (PaginationContext) context.data().get(PaginationContext.DATA_ATTR);
            String linkHeader = pageContext.buildLinkHeader(context.request());
            if (linkHeader != null) {
                context.response().headers().add("Link", linkHeader);
            } else {
                logger.log(Level.WARNING, "You did not set the total count on PaginationContext, response won't be paginated");
            }
            context.next();
        });
    }

    @Override
    public String toString() {
        return "Route : " + httpMethod.toString() + " " + path();
    }

}
