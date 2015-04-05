package io.vertx.mvc.marshallers;

public class Payload<T> {
	private T userPayload;
	
	public void set(T userPayload) {
		this.userPayload = userPayload;
	}
	
	public T get() {
		return userPayload;
	}
}
