package io.vertx.mvc.handlers.impl;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.annotations.params.Header;
import io.vertx.mvc.annotations.params.Param;
import io.vertx.mvc.annotations.params.Params;
import io.vertx.mvc.annotations.params.PathParam;
import io.vertx.mvc.annotations.params.RequestBody;
import io.vertx.mvc.context.PaginationContext;
import io.vertx.mvc.exceptions.BadRequestException;
import io.vertx.mvc.exceptions.HttpException;
import io.vertx.mvc.marshallers.Payload;
import io.vertx.mvc.reflections.ParameterAdapterRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodInvocationHandler implements Handler<RoutingContext> {

	private static final Logger log = LoggerFactory.getLogger(MethodInvocationHandler.class);
	
	private Method method;
	private Object instance;
	private Payload<?> payload;
	private ParameterAdapterRegistry adapters;
	
	public MethodInvocationHandler(Object instance, Method method, ParameterAdapterRegistry adapters, Payload<?> payload) {
		this.method = method;
		this.adapters = adapters;
		this.instance = instance;
		this.adapters = adapters;
		this.payload = payload;
	}
	
	@Override
	public void handle(RoutingContext routingContext) {
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
			log.error(others);
			routingContext.fail(others);
		}

	}
	
	private Object getParameterInstance(RoutingContext context, Annotation[] annotations, Class<?> parameterClass) throws BadRequestException {
		if (annotations.length == 0) {
			if (parameterClass.equals(RoutingContext.class)) {
				return context;
			} else if (parameterClass.equals(Vertx.class)) {
				return context.vertx();
			} else if (parameterClass.equals(PaginationContext.class)) {
				return context.get(PaginationContext.DATA_ATTR);
			} else if (parameterClass.equals(Payload.class)) {
				return payload;
			} 
			return null;
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
			paramValue = adapters.adaptParams(params, parameterClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("Parameters are invalid", e);
		}
		return paramValue;
	}

	public Object fromRequestParam(String name, String value, boolean mandatory, Class<?> parameterClass) throws BadRequestException {
		Object paramValue;
		try {
			paramValue = adapters.adaptParam(value, parameterClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(name + " is invalid");
		}
		if (mandatory && paramValue == null) {
			throw new BadRequestException("Query parameter : " + name + " is mandatory");
		}
		return paramValue;
	}
}