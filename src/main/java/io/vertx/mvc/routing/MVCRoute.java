package io.vertx.mvc.routing;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.BodyHandler;
import io.vertx.ext.apex.handler.CookieHandler;
import io.vertx.ext.apex.handler.TemplateHandler;
import io.vertx.ext.apex.impl.Utils;
import io.vertx.mvc.annotations.params.Header;
import io.vertx.mvc.annotations.params.Param;
import io.vertx.mvc.annotations.params.Params;
import io.vertx.mvc.annotations.params.PathParam;
import io.vertx.mvc.annotations.params.RequestBody;
import io.vertx.mvc.context.ClientAccesses;
import io.vertx.mvc.context.PaginationContext;
import io.vertx.mvc.context.RateLimit;
import io.vertx.mvc.controllers.ApiController;
import io.vertx.mvc.controllers.impl.JsonApiController;
import io.vertx.mvc.exceptions.BadRequestException;
import io.vertx.mvc.exceptions.HttpException;
import io.vertx.mvc.reflections.ParameterAdapter;

import java.lang.annotation.Annotation;
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
	private boolean usesCookies;
	private Class<?> bodyClass;
	private String viewName;
	private TemplateHandler templateHandler;

	public MVCRoute(Object instance, String path, boolean paginated, TemplateHandler templateHandler) {
		this(instance, path, HttpMethod.GET, paginated, templateHandler);
	}

	public MVCRoute(Object instance, String path, HttpMethod method, boolean paginated, TemplateHandler templateHandler) {
		this.instance = instance;
		this.path = path;
		this.httpMethod = method;
		this.beforeFilters = new ArrayList<Method>();
		this.afterFilters = new ArrayList<Method>();
		this.paginated = paginated;
		this.templateHandler = templateHandler;
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

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public boolean isView() {
		return viewName != null;
	}

	public boolean isApi() {
		return instance instanceof ApiController;
	}

	public boolean isJsonApi() {
		return instance instanceof JsonApiController;
	}

	public void setBodyClass(Class<?> bodyClass) {
		this.bodyClass = bodyClass;
	}

	public void attachHandlersToRouter(Router router, CookieHandler cookieHandler) {
		if (cookieHandler != null) {
			router.route(httpMethod, path).handler(cookieHandler);
		}
		if (isJsonApi()) {
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
		if (needsBodyHandling()) {
			attachBodyHandler(router);
		}
		setHandler(router, mainHandler);
		afterFilters.forEach(filter -> {
			setHandler(router, filter);
		});
		if (paginated && isApi()) {
			setPaginationOnResponse(router);
		}
		if (finalizer != null) {
			setHandler(router, finalizer);
		} else if (isView()) {
			router.route(httpMethod, path).handler(context -> {
				context.data().put("tplName", viewName);
				context.next();
			});
			router.route(httpMethod, path).handler(templateHandler);
		}
	}

	private boolean needsBodyHandling() {
		return bodyClass != null ||
				httpMethod == HttpMethod.POST ||
				httpMethod == HttpMethod.PUT;
	}

	private void checkContentType(Router router, String contentType) {
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

	private void attachBodyHandler(Router router) {
		router.route(httpMethod, path).handler(BodyHandler.create());
		router.route(httpMethod, path).handler(context -> {
			if (isApi()) {
				String body = context.getBodyAsString();
				context.data().put("body", ((ApiController)instance).fromRequestBody(bodyClass, body));
			}
			context.next();
		});
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
				method.invoke(instance, parameters.toArray());
			} catch (HttpException he) {
				routingContext.response().setStatusCode(he.getStatusCode());
				routingContext.response().setStatusMessage(he.getStatusMessage());
				routingContext.fail(he.getStatusCode());
			} catch (Throwable others) {
				routingContext.fail(others);
			}
		});
	}

	private Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass) throws BadRequestException {
		if (annotations.length == 0) {
			if (parameterClass.equals(RoutingContext.class)) {
				return context;
			} else if (parameterClass.equals(Vertx.class)) {
				return context.vertx();
			} else if (parameterClass.equals(PaginationContext.class)) {
				return context.get(PaginationContext.DATA_ATTR);
			} else {
				// TODO : try to map context params on object ?
				return null;
			}
		}
		if (annotations.length > 1) {
			throw new IllegalArgumentException("Every parameter should only have ONE annotation");
		}
		HttpServerRequest request = context.request();
		Annotation annotation = annotations[0];
		if (annotation instanceof PathParam) {
			PathParam param = (PathParam)annotation;
			String name = param.value();
			String value = request.getParam(name);
			return fromRequestParam(name, value, false, parameterClass);
		} else if (annotation instanceof Param) {
			Param param = (Param) annotation;
			String name = param.value();
			String value = request.getParam(name);
			boolean mandatory = param.mandatory();
			return fromRequestParam(name, value, mandatory, parameterClass);
		} else if (annotation instanceof Params) {
			return fromRequestParams(context.request().params(), parameterClass);
		} else if (annotation instanceof RequestBody) {
			return context.data().get("body");
		} else if (annotation instanceof Header) {
			Header header = (Header)annotation;
			String name = header.value();
			String value = request.getHeader(name);
			boolean mandatory = header.mandatory();
			return fromRequestParam(name, value, mandatory, parameterClass);
		}
		return null;
	}

	public Object fromRequestParams(MultiMap params, Class<?> parameterClass) throws BadRequestException {
		Object paramValue;
		try {
			paramValue = ParameterAdapter.adaptParamsToType(params, parameterClass);
		} catch (Exception e) {
			throw new BadRequestException("Parameters are invalid", e);
		}
		return paramValue;

	}

	public Object fromRequestParam(String name, String value, boolean mandatory, Class<?> parameterClass) throws BadRequestException {
		Object paramValue;
		try {
			paramValue = ParameterAdapter.adaptParamToType(value, parameterClass);
		} catch (Exception e) {
			throw new BadRequestException(name + " is invalid");
		}
		if (mandatory && paramValue == null) {
			throw new BadRequestException("Query parameter : " + name + " is mandatory");
		}
		return paramValue;
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
