package io.vertx.mvc;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.controllers.AbstractController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MVCRoute implements Handler<RoutingContext> {

	private final String path;
	private final HttpMethod httpMethod;
	private final AbstractController instance;
	private List<Method> beforeFilters;
	private List<Method> afterFilters;
	private Method mainHandler;
	private Method finalizer;

	
	public MVCRoute(AbstractController instance, String path) {
		this(instance, path,  HttpMethod.GET);
	}
	
	public MVCRoute(AbstractController instance, String path, HttpMethod method) {
		this.instance = instance;
		this.path = path;
		this.httpMethod = method;
		this.beforeFilters = new ArrayList<Method>();
		this.afterFilters = new ArrayList<Method>();
	}
	
	public void setMainHandler(Method mainHandler) {
		this.mainHandler = mainHandler;
	}
	
	public void setFinalizer(Method finalizer) {
		this.finalizer = finalizer;
	}
	
	public void addBeforeFilters(List<Method> beforeFilters) {
		this.beforeFilters.addAll(beforeFilters);
	}
	
	public void addAfterFilters(List<Method> afterFilters) {
		this.afterFilters.addAll(afterFilters);
	}
	
	@Override
	public void handle(RoutingContext context) {
		beforeFilters.forEach(filter -> {
			try {
				filter.invoke(instance, context);
			} catch (Exception e) {
				context.fail(e);
				return;
			}
		});
		try {
			mainHandler.invoke(instance, context);
		} catch(Exception e) {
			context.fail(e);
			return;
		}
		afterFilters.forEach(filter -> {
			try {
				filter.invoke(instance, context);
			} catch(Exception e) {
				context.fail(e);
				return;
			}
		});
		if (finalizer != null) {
			try {
				finalizer.invoke(instance, context);
			} catch(Exception e) {
				context.fail(e);
			}
		}
	}
	
	public String path() {
		return path;
	}
	
	public HttpMethod method(){
		return httpMethod;
	}

}
