package com.github.aesteve.vertx.nubes.views.impl;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.Utils;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.ext.web.templ.impl.CachingTemplateEngine;

public class PrefixedHandlebarsTemplateEngineImpl extends CachingTemplateEngine<Template> implements HandlebarsTemplateEngine {
    private final Handlebars handlebars;
    private final Loader loader = new Loader();
    private final String prefix;

    public PrefixedHandlebarsTemplateEngineImpl(String prefix) {
        super(HandlebarsTemplateEngine.DEFAULT_TEMPLATE_EXTENSION, HandlebarsTemplateEngine.DEFAULT_MAX_CACHE_SIZE);
        this.prefix = prefix;
        handlebars = new Handlebars(loader);
    }

    @Override
    public HandlebarsTemplateEngine setExtension(String extension) {
        doSetExtension(extension);
        return this;
    }

    @Override
    public HandlebarsTemplateEngine setMaxCacheSize(int maxCacheSize) {
        this.cache.setMaxSize(maxCacheSize);
        return null;
    }

    @Override
    public void render(RoutingContext context, String templateFileName, Handler<AsyncResult<Buffer>> handler) {
        try {
            Template template = cache.get(templateFileName);
            if (template == null) {
                synchronized (this) {
                    loader.setVertx(context.vertx());
                    template = handlebars.compile(templateFileName);
                    cache.put(templateFileName, template);
                }
            }
            handler.handle(Future.succeededFuture(Buffer.buffer(template.apply(context.data()))));
        } catch (Exception ex) {
            handler.handle(Future.failedFuture(ex));
        }
    }

    private class Loader implements TemplateLoader {

        private Vertx vertx;

        void setVertx(Vertx vertx) {
            this.vertx = vertx;
        }

        @Override
        public TemplateSource sourceAt(String location) throws IOException {
            String separator = "";
            if (prefix.endsWith("/") && !location.startsWith("/")) {
                separator = "/";
            }
            location = prefix + separator + location;

            String loc = adjustLocation(location);
            String templ = Utils.readFileToString(vertx, loc);

            if (templ == null) {
                throw new IllegalArgumentException("Cannot find resource " + loc);
            }

            long lastMod = System.currentTimeMillis();

            return new TemplateSource() {
                @Override
                public String content() throws IOException {
                    // load from the file system
                    return templ;
                }

                @Override
                public String filename() {
                    return loc;
                }

                @Override
                public long lastModified() {
                    return lastMod;
                }
            };
        }

        @Override
        public String resolve(String location) {
            return location;
        }

        @Override
        public String getPrefix() {
            return prefix;
        }

        @Override
        public String getSuffix() {
            return extension;
        }
    }

    @Override
    public Handlebars getHandlebars() {
        return handlebars;
    }
}
