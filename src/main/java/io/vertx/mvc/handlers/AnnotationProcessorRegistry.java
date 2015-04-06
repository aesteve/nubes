package io.vertx.mvc.handlers;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class AnnotationProcessorRegistry {
	
	private Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> map;
	
	public AnnotationProcessorRegistry() {
		map = new HashMap<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>>();
	}
	
	public<T extends Annotation> void registerProcessor(Class<T> annotation, AnnotationProcessor<T> processor) {
		map.put(annotation, processor);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Annotation> AnnotationProcessor<T> getProcessor(T annotation) {
		AnnotationProcessor<T> processor = (AnnotationProcessor<T>)getProcessor(annotation.annotationType());
		if (processor == null) {
			return null;
		}
		return processor;
	}
	
	@SuppressWarnings("unchecked")
	private<T extends Annotation> AnnotationProcessor<T> getProcessor(Class<T> annotation) {
		return (AnnotationProcessor<T>)map.get(annotation);
	}
}
