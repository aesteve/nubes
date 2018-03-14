package com.github.aesteve.vertx.nubes.reflections.annotations.impl;

import com.github.aesteve.vertx.nubes.reflections.annotations.IAnnotationProvider;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class FastclassPathScannerProvider implements IAnnotationProvider {

    private static Map<String, ScanResult> cachedScans = new HashMap();

    private String packageName;

    public FastclassPathScannerProvider(String ppackage){
        packageName = ppackage;

        if (cachedScans.containsKey(ppackage)){
        }else{
            FastClasspathScanner fastClasspathScanner = new FastClasspathScanner(ppackage);
            ScanResult scanResult = fastClasspathScanner.scan();
            cachedScans.put(ppackage, scanResult);
        }
    }

    @Override
    public Set<Class<?>> getClassesTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return new HashSet(cachedScans.get(packageName).classNamesToClassRefs(cachedScans.get(packageName).getNamesOfClassesWithAnnotation(annotation)));
    }

    @Override
    public <T> Set<Class<? extends T>> getSubClassOf(Class<T> clazz) {
        if (clazz.isInterface()){
            return new HashSet(cachedScans.get(packageName).classNamesToClassRefs(cachedScans.get(packageName).getNamesOfClassesImplementing(clazz)));
        }
        return new HashSet(cachedScans.get(packageName).classNamesToClassRefs(cachedScans.get(packageName).getNamesOfSubclassesOf(clazz)));
    }
}
