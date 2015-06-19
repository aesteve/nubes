package com.github.aesteve.vertx.nubes;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.templ.TemplateEngine;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.github.aesteve.vertx.nubes.auth.AuthMethod;
import com.github.aesteve.vertx.nubes.context.RateLimit;
import com.github.aesteve.vertx.nubes.exceptions.MissingConfigurationException;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessorRegistry;
import com.github.aesteve.vertx.nubes.handlers.Processor;
import com.github.aesteve.vertx.nubes.reflections.RouteRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.reflections.injectors.typed.TypedParamInjectorRegistry;
import com.github.aesteve.vertx.nubes.services.ServiceRegistry;

public class Config {

    private static Config instance;

    public static Config instance() {
        return instance;
    }

    private Config() {
        bundlesByLocale = new HashMap<Locale, ResourceBundle>();
        globalHandlers = new ArrayList<Handler<RoutingContext>>();
        templateEngines = new HashMap<String, TemplateEngine>();
        sockJSOptions = new SockJSHandlerOptions();
    }

    public List<String> controllerPackages;
    public List<String> fixturePackages;
    public String domainPackage;
    public RateLimit rateLimit;
    public String webroot;
    public String assetsPath;
    public String tplDir;
    public boolean displayErrors;
    public Vertx vertx;
    public AuthProvider authProvider;
    public AuthMethod authMethod;
    public String i18nDir;

    public AnnotationProcessorRegistry apRegistry;
    public Map<Class<? extends Annotation>, Set<Handler<RoutingContext>>> annotationHandlers;
    public Map<Class<?>, Processor> typeProcessors;
    public TypedParamInjectorRegistry typeInjectors;
    public AnnotatedParamInjectorRegistry annotInjectors;
    public ServiceRegistry serviceRegistry;
    public RouteRegistry routeRegistry;
    public Map<Class<?>, Handler<RoutingContext>> paramHandlers;
    public Map<String, Handler<RoutingContext>> aopHandlerRegistry;
    public Map<Locale, ResourceBundle> bundlesByLocale;
    public List<Handler<RoutingContext>> globalHandlers;
    public Map<String, TemplateEngine> templateEngines;
    public SockJSHandlerOptions sockJSOptions;

    /**
     * TODO : check config instead of throwing exceptions
     * 
     * @param json
     * @return config
     */
    @SuppressWarnings("unchecked")
    public static Config fromJsonObject(JsonObject json, Vertx vertx) throws MissingConfigurationException {
        instance = new Config();
        instance.vertx = vertx;
        instance.i18nDir = json.getString("i18nDir", "web/i18n/");
        if (!instance.i18nDir.endsWith("/")) {
            instance.i18nDir = instance.i18nDir + "/";
        }
        JsonArray controllers = json.getJsonArray("controller-packages");
        if (controllers == null) {
            throw new MissingConfigurationException("controller-packages");
        }
        instance.controllerPackages = controllers.getList();
        instance.domainPackage = json.getString("domain-package");
        JsonArray fixtures = json.getJsonArray("fixture-packages");
        if (fixtures != null) {
            instance.fixturePackages = fixtures.getList();
        } else {
            instance.fixturePackages = new ArrayList<String>();
        }
        JsonObject rateLimitJson = json.getJsonObject("throttling");
        if (rateLimitJson != null) {
            int count = rateLimitJson.getInteger("count");
            int value = rateLimitJson.getInteger("time-frame");
            TimeUnit timeUnit = TimeUnit.valueOf(rateLimitJson.getString("time-unit"));
            instance.rateLimit = new RateLimit(count, value, timeUnit);
        }
        instance.webroot = json.getString("webroot", "web/assets");
        instance.assetsPath = json.getString("static-path", "/assets");
        instance.tplDir = json.getString("views-dir", "web/views");
        instance.displayErrors = json.getBoolean("display-errors", Boolean.FALSE);
        // TODO : read sockJSOptions from config
        return instance;
    }

    public ResourceBundle getResourceBundle(Locale loc) {
        return bundlesByLocale.get(loc);
    }
}
