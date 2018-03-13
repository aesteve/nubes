package com.github.aesteve.vertx.nubes.reflections.annotations;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface IAnnotationProvider {
    public Set<Class<?>> getClassesTypesAnnotatedWith(final Class<? extends Annotation> annotation);
}
