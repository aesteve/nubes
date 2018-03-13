package com.github.aesteve.vertx.nubes.reflections.annotations.impl;

import com.github.aesteve.vertx.nubes.reflections.annotations.IAnnotationProvider;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ReflectionsAnnotationProvider implements IAnnotationProvider {

    private Reflections reflections;

    public ReflectionsAnnotationProvider(String packaga){
        reflections = new Reflections();
    }

    @Override
    public Set<Class<?>> getClassesTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
