package com.github.aesteve.vertx.nubes.handlers.impl;

import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.handlers.AbstractMethodInvocationHandler;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

public class DefaultMethodInvocationHandler<T> extends AbstractMethodInvocationHandler<T> {

	public DefaultMethodInvocationHandler(Object instance, Method method, Config config, boolean hasNext, BiConsumer<RoutingContext, T> returnHandler) {
		super(instance, method, config, hasNext, returnHandler);
	}

	@Override
	public void handle(RoutingContext routingContext) {
		if (routingContext.response().ended()) {
			return;
		}
		if (routingContext.failed()) {
			return;
		}
		Object[] parameters = null;
		try {
			parameters = getParameters(routingContext);
		} catch (Exception e) {
			routingContext.fail(400);
			return;
		}
		try {
			@SuppressWarnings("unchecked")
			T returned = (T) method.invoke(instance, parameters);
			if (returnsSomething) {
				boolean contentTypeSet = routingContext.get(ContentTypeProcessor.BEST_CONTENT_TYPE) != null;
				if (returnHandler != null) {
					returnHandler.accept(routingContext, returned);
				} else if (hasNext && contentTypeSet) {
					// try to set as Payload
					Payload<Object> payload = routingContext.get(Payload.DATA_ATTR);
					if (payload == null) {
						payload = new Payload<>();
						routingContext.put(Payload.DATA_ATTR, payload);
					}
					payload.set(returned);
				} else if (returned instanceof String) {
					routingContext.response().end((String) returned);
				}
			}
			if (!usesRoutingContext && hasNext) { // cannot call context.next(), assume the method is sync
				routingContext.next();
			}
		} catch (InvocationTargetException ite) {
			routingContext.fail(ite.getCause());
			return;
		} catch (Throwable others) {
			routingContext.fail(others);
			return;
		}
	}
}