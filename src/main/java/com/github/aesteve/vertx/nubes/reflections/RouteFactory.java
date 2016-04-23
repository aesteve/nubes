package com.github.aesteve.vertx.nubes.reflections;

import com.github.aesteve.vertx.nubes.reflections.visitors.ControllerVisitor;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import io.vertx.ext.web.Session;
import org.reflections.Reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.auth.Auth;
import com.github.aesteve.vertx.nubes.annotations.filters.After;
import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.Before;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import com.github.aesteve.vertx.nubes.annotations.routing.Disabled;
import com.github.aesteve.vertx.nubes.annotations.routing.Forward;
import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import com.github.aesteve.vertx.nubes.context.FileResolver;
import com.github.aesteve.vertx.nubes.context.ViewResolver;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.reflections.factories.AuthenticationFactory;
import com.github.aesteve.vertx.nubes.routing.HttpMethodFactory;
import com.github.aesteve.vertx.nubes.routing.MVCRoute;

public class RouteFactory implements HandlerFactory {

	private final Router router;
	private final Config config;
	private final AuthenticationFactory authFactory;
	private final RouteRegistry routeRegistry;
	private Map<Class<? extends Annotation>, BiConsumer<RoutingContext, ?>> returnHandlers;

	public RouteFactory(Router router, Config config) {
		this.router = router;
		this.config = config;
		routeRegistry = new RouteRegistry();
		authFactory = new AuthenticationFactory(config);
		createReturnHandlers();
	}

	private void createReturnHandlers() {
		returnHandlers = new HashMap<>();
		returnHandlers.put(View.class, (context, res) -> {
			if (res instanceof String) {
				ViewResolver.resolve(context, (String) res);
			} else if (res instanceof Map) {
				@SuppressWarnings("unchecked") // we have to...
				Map<? extends String, ?> mapRes = (Map<? extends String, ?>) res;
				context.data().putAll(mapRes);
			}
		});
		returnHandlers.put(File.class, (context, res) -> FileResolver.resolve(context, (String) res));
	}

	public void createHandlers() {
		List<MVCRoute> routes = extractRoutesFromControllers();
		routes.stream().filter(MVCRoute::isEnabled).forEach(route -> route.attachHandlersToRouter(router));
	}

	private List<MVCRoute> extractRoutesFromControllers() {
		List<MVCRoute> routes = new ArrayList<>();
		config.forEachControllerPackage(controllerPackage -> {
			Reflections reflections = new Reflections(controllerPackage);
			Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
			controllers.forEach(controller -> routes.addAll(extractRoutesFromController(controller)));
		});
		return routes;
	}

	private List<MVCRoute> extractRoutesFromController(Class<?> controller) {
		try {
			ControllerVisitor<?> visitor = new ControllerVisitor<>(controller, config, router, authFactory, routeRegistry, returnHandlers);
			return visitor.visit();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new VertxException(e);
		}
	}

}
