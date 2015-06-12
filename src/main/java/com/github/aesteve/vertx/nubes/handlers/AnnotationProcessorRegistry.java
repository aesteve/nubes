package com.github.aesteve.vertx.nubes.handlers;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class AnnotationProcessorRegistry {

    private Map<Class<? extends Annotation>, AnnotationProcessorFactory<? extends Annotation>> map;

    public AnnotationProcessorRegistry() {
        map = new HashMap<Class<? extends Annotation>, AnnotationProcessorFactory<? extends Annotation>>();
    }

    public <T extends Annotation> void registerProcessor(Class<T> annotation, AnnotationProcessorFactory<T> processor) {
        map.put(annotation, processor);
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> AnnotationProcessor<T> getProcessor(T annotation) {
        AnnotationProcessorFactory<T> processor = (AnnotationProcessorFactory<T>) getProcessor(annotation.annotationType());
        if (processor == null) {
            return null;
        }
        return processor.create(annotation);
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> AnnotationProcessorFactory<T> getProcessor(Class<T> annotation) {
        return (AnnotationProcessorFactory<T>) map.get(annotation);
    }
}
