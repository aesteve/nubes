package io.vertx.nubes.reflections.factories.impl;

import io.vertx.nubes.annotations.View;
import io.vertx.nubes.handlers.AnnotationProcessor;
import io.vertx.nubes.handlers.impl.ViewProcessor;
import io.vertx.nubes.reflections.factories.AnnotationProcessorFactory;
import io.vertx.nubes.views.TemplateEngineManager;

public class ViewProcessorFactory implements AnnotationProcessorFactory<View> {

    private TemplateEngineManager tplEngineMgr;

    public ViewProcessorFactory(TemplateEngineManager tplEngineMgr) {
        this.tplEngineMgr = tplEngineMgr;
    }

    @Override
    public AnnotationProcessor<View> create(View annotation) {
        return new ViewProcessor(tplEngineMgr, annotation);
    }

}
