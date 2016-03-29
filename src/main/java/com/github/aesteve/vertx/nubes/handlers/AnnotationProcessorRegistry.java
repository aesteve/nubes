package com.github.aesteve.vertx.nubes.handlers;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class AnnotationProcessorRegistry {

	private final Map<Class<? extends Annotation>, AnnotationProcessorFactory<? extends Annotation>> factoryMap;
	private final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> processorMap;

	public AnnotationProcessorRegistry() {
		factoryMap = new HashMap<>();
		processorMap = new HashMap<>();
	}

	public <T extends Annotation> void registerProcessor(Class<T> annotation, AnnotationProcessorFactory<T> processor) {
		factoryMap.put(annotation, processor);
	}

	public <T extends Annotation> void registerProcessor(Class<T> annotation, AnnotationProcessor<T> processor) {
		processorMap.put(annotation, processor);
	}

	@SuppressWarnings("unchecked")
	public <T extends Annotation> AnnotationProcessor<T> getProcessor(T annotation) {
		AnnotationProcessorFactory<T> factory = (AnnotationProcessorFactory<T>) getFactory(annotation.annotationType());
		if (factory != null) {
			return factory.create(annotation);
		}
		return getSimpleProcessor(annotation);
	}

	@SuppressWarnings("unchecked")
	private <T extends Annotation> AnnotationProcessor<T> getSimpleProcessor(T annotation) {
		return (AnnotationProcessor<T>) processorMap.get(annotation.annotationType());
	}

	@SuppressWarnings("unchecked")
	private <T extends Annotation> AnnotationProcessorFactory<T> getFactory(Class<T> annotation) {
		return (AnnotationProcessorFactory<T>) factoryMap.get(annotation);
	}
}
