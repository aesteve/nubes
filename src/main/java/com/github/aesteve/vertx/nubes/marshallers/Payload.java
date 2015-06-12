package com.github.aesteve.vertx.nubes.marshallers;

public class Payload<T> {
	
	public static final String DATA_ATTR = "user-payload";
	
	private T userPayload;
	
	public void set(T userPayload) {
		this.userPayload = userPayload;
	}
	
	public T get() {
		return userPayload;
	}
}
