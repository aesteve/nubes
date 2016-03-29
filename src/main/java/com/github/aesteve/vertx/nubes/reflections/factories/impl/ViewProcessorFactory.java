package com.github.aesteve.vertx.nubes.reflections.factories.impl;

import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.ViewProcessor;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;
import com.github.aesteve.vertx.nubes.views.TemplateEngineManager;

public class ViewProcessorFactory implements AnnotationProcessorFactory<View> {

	private final TemplateEngineManager tplEngineMgr;

	public ViewProcessorFactory(TemplateEngineManager tplEngineMgr) {
		this.tplEngineMgr = tplEngineMgr;
	}

	@Override
	public AnnotationProcessor<View> create(View annotation) {
		return new ViewProcessor(tplEngineMgr, annotation);
	}

}
