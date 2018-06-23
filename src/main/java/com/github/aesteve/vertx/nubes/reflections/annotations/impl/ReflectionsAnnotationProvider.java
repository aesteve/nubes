package com.github.aesteve.vertx.nubes.reflections.annotations.impl;

import com.github.aesteve.vertx.nubes.reflections.annotations.IAnnotationProvider;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import javafx.util.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
//ToDo work out a way to add exclude property to Reflections
public class ReflectionsAnnotationProvider implements IAnnotationProvider {

    private static Map<String, Reflections> cachedScans = new HashMap();

    private String packageName;

    public ReflectionsAnnotationProvider(String packaga){
        packageName = packaga;
        if (!cachedScans.containsKey(packaga)){
            cachedScans.put(packaga, new Reflections(packaga));
        }
    }

    @Override
    public Set<Class<?>> getClassesTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return cachedScans.get(packageName).getTypesAnnotatedWith(annotation);
    }

    @Override
    public <T> Set<Class<? extends T>> getSubClassOf(Class<T> clazz) {
        return cachedScans.get(packageName).getSubTypesOf(clazz);
    }
}
