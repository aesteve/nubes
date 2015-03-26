package io.vertx.mvc.routing;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.CookieHandler;
import io.vertx.ext.apex.impl.Utils;
import io.vertx.mvc.annotations.params.PathParameter;
import io.vertx.mvc.annotations.params.QueryParameter;
import io.vertx.mvc.context.ClientAccesses;
import io.vertx.mvc.context.PaginationContext;
import io.vertx.mvc.context.RateLimit;
import io.vertx.mvc.controllers.ApiController;
import io.vertx.mvc.controllers.impl.JsonApiController;
import io.vertx.mvc.exceptions.BadRequestException;
import io.vertx.mvc.exceptions.HttpException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
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
    private boolean usesCookies;

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

    public boolean usesCookies() {
        return usesCookies;
    }

    public void usesCookies(boolean uses) {
        usesCookies = uses;
    }

    public void attachHandlersToRouter(Router router, CookieHandler cookieHandler) {
        if (cookieHandler != null) {
            router.route(httpMethod, path).handler(cookieHandler);
        }
        if (instance instanceof JsonApiController) {
            checkContentType(router, "application/json");
        }
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

    public void checkContentType(Router router, String contentType) {
        router.route(httpMethod, path).handler(context -> {
            String accept = context.request().getHeader("accept");
            if (accept == null) {
                context.fail(406);
                return;
            }
            List<String> acceptableTypes = Utils.getSortedAcceptableMimeTypes(accept);
            if (acceptableTypes.contains("application/json")) {
                context.response().putHeader("Content-Type", "application/json");
                context.next();
            } else {
                context.fail(406);
            }
        });
    }

    public void attachHandlersToRouter(Router router) {
        attachHandlersToRouter(router, null);
    }

    private void setHandler(Router router, Method method) {
        router.route(httpMethod, path).handler(routingContext -> {
            if (routingContext.response().ended()) {
                return;
            }
            try {
                List<Object> parameters = new ArrayList<Object>();
                Class<?>[] parameterClasses = method.getParameterTypes();
                Annotation[][] parametersAnnotations = method.getParameterAnnotations();
                for (int i = 0; i < parameterClasses.length; i++) {
                    Object paramInstance = getParameterInstance(routingContext, parametersAnnotations[i], parameterClasses[i]);
                    parameters.add(paramInstance);
                }
                method.invoke(instance, routingContext);
            } catch (Throwable e) {
                e.printStackTrace();
                routingContext.fail(e);
            }
        });
    }

    private Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass) throws BadRequestException {
        if (annotations.length == 0) {
            return context;
        }
        if (annotations.length > 1) {
            throw new IllegalArgumentException("Every parameter should only have ONE annotation");
        }
        Annotation annotation = annotations[0];
        if (annotation instanceof PathParameter) {
            String name = ((PathParameter) annotation).value();
            String value = context.request().getParam(name);
            try {
                return adaptParamToType(value, parameterClass);
            } catch (Exception e) {
                throw new BadRequestException(name + " is invalid");
            }
        } else if (annotation instanceof QueryParameter) {
            String name = ((QueryParameter) annotation).value();
            String value = context.request().getParam(name);
            try {
                return adaptParamToType(value, parameterClass);
            } catch (Exception e) {
                throw new BadRequestException(name + " is invalid");
            }
        }
        return null;
    }

    private Object adaptParamToType(String value, Class<?> parameterClass) {
        if (parameterClass.equals(String.class)) {
            return value;
        } else if (parameterClass.equals(Long.class)) {
            return Long.valueOf(value);
        } else if (parameterClass.equals(Integer.class)) {
            return Integer.valueOf(value);
        } else if (parameterClass.equals(Date.class)) {
            // TODO : parse ISO
            return null;
        }
        return null;
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
