package io.vertx.mvc.routing;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.TemplateHandler;
import io.vertx.mvc.handlers.Processor;
import io.vertx.mvc.handlers.impl.MethodInvocationHandler;
import io.vertx.mvc.marshallers.Payload;
import io.vertx.mvc.marshallers.PayloadMarshaller;
import io.vertx.mvc.reflections.ParameterAdapterRegistry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MVCRoute {

	private final String path;
	private final HttpMethod httpMethod;
	private final Object instance;
	private List<Method> beforeFilters;
	private List<Method> afterFilters;
	private Method mainHandler;
	private Class<?> bodyClass;
	private String viewName;
	private TemplateHandler templateHandler;
	private ParameterAdapterRegistry adapters;
	private Set<Handler<RoutingContext>> handlers;
	private Set<Processor> processors;
	private Map<String, PayloadMarshaller> marshallers;
	private Payload<?> payload;

	@SuppressWarnings("rawtypes")
	public MVCRoute(Object instance, 
			String path, 
			HttpMethod method, 
			TemplateHandler templateHandler, 
			ParameterAdapterRegistry adapters, 
			Map<String, PayloadMarshaller> marshallers) {
		this.instance = instance;
		this.path = path;
		this.httpMethod = method;
		this.beforeFilters = new ArrayList<Method>();
		this.afterFilters = new ArrayList<Method>();
		this.templateHandler = templateHandler;
		this.adapters = adapters;
		this.handlers = new LinkedHashSet<Handler<RoutingContext>>();
		this.processors = new LinkedHashSet<Processor>();
		this.marshallers = marshallers;
		this.payload = new Payload();
	}
	
	public void addProcessor(Processor processor) {
		processors.add(processor);
	}
	
	public void addProcessors(Set<Processor> processors) {
		this.processors.addAll(processors);
	}
	
	public void addProcessorsFirst(Set<Processor> processors) {
		processors.addAll(this.processors);
		this.processors = processors;
	}
	
	public void attachHandler(Handler<RoutingContext> handler) {
		handlers.add(handler);
	}
	
	public void attachHandlers(Set<Handler<RoutingContext>> newHandlers) {
		handlers.addAll(newHandlers);
	}

	public void setMainHandler(Method mainHandler) {
		this.mainHandler = mainHandler;
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
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public boolean isView() {
		return viewName != null;
	}

	public void setBodyClass(Class<?> bodyClass) {
		this.bodyClass = bodyClass;
	}

	public void attachHandlersToRouter(Router router) {
		handlers.forEach(handler -> {
			router.route(httpMethod, path).handler(handler);
		});
		processors.forEach(processor -> {
			router.route(httpMethod, path).handler(processor::preHandle);
		});
		beforeFilters.forEach(filter -> {
			setHandler(router, filter);
		});
		if (needsBodyHandling()) {
			attachBodyHandler(router);
		}
		setHandler(router, mainHandler);
		processors.forEach(processor -> {
			router.route(httpMethod, path).handler(processor::postHandle);
		});
		afterFilters.forEach(filter -> {
			setHandler(router, filter);
		});
		if (isView()) {
			router.route(httpMethod, path).handler(context -> {
				context.data().put("tplName", viewName);
				context.next();
			});
			router.route(httpMethod, path).handler(templateHandler);
		} else {
			setPayloadHandler(router);
		}
	}

	private boolean needsBodyHandling() {
		return bodyClass != null;
	}

	private void attachBodyHandler(Router router) {
		router.route(httpMethod, path).handler(context -> {
			String body = context.getBodyAsString();
			System.out.println("attach body handler "+body);
			if (body != null) {
				System.out.println("body is not null");
				PayloadMarshaller marshaller = marshallers.get(context.get("best-content-type"));
				context.data().put("body", marshaller.unmarshallPayload(body, bodyClass));
			}
			context.next();
		});
	}

	private void setHandler(Router router, Method method) {
		router.route(httpMethod, path).handler(new MethodInvocationHandler(instance, method, adapters, payload));
	}
	
	private void setPayloadHandler(Router router) {
		router.route(httpMethod, path).handler(context -> {
			HttpServerResponse response = context.response(); 
			if (response.ended()) {
				return;
			}
			Object userPayload = payload.get();
			if (userPayload == null) {
				response.setStatusCode(204);
				response.end();
			} else {
				PayloadMarshaller marshaller = marshallers.get(context.get("best-content-type"));
				response.setStatusCode(200);
				response.end(marshaller.marshallPayload(userPayload));
			}
		});
	}

	@Override
	public String toString() {
		return "Route : " + httpMethod.toString() + " " + path();
	}	
}
