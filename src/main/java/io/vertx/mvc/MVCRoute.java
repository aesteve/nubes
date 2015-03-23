package io.vertx.mvc;

import io.vertx.core.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MVCRoute {

	private final String path;
	private final HttpMethod httpMethod;
	private final Object instance;
	private List<Method> beforeFilters;
	private List<Method> afterFilters;
	private Method mainHandler;
	private Method finalizer;

	
	public MVCRoute(Object instance, String path) {
		this(instance, path,  HttpMethod.GET);
	}
	
	public MVCRoute(Object instance, String path, HttpMethod method) {
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
	
	public List<Method> beforeFilters(){
		return beforeFilters;
	}
	
	public List<Method> afterFilters(){
		return afterFilters;
	}
	
	public Method mainHandler(){
		return mainHandler;
	}
	
	public Method finalizer(){
		return finalizer;
	}
	
	public String path() {
		return path;
	}
	
	public HttpMethod method(){
		return httpMethod;
	}
	
	public Object controller(){
		return instance;
	}
	
	@Override
	public String toString(){
		return "Route : " + httpMethod.toString() + " " + path();
	}

}
