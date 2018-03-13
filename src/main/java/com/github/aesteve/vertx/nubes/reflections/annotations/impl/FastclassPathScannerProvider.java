package com.github.aesteve.vertx.nubes.reflections.annotations.impl;

import com.github.aesteve.vertx.nubes.reflections.annotations.IAnnotationProvider;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FastclassPathScannerProvider implements IAnnotationProvider {

    private ScanResult scanResult;

    public FastclassPathScannerProvider(String ppackage){
        FastClasspathScanner fastClasspathScanner = new FastClasspathScanner(ppackage);
        scanResult = fastClasspathScanner.scan();
    }

    @Override
    public Set<Class<?>> getClassesTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return new HashSet(scanResult.classNamesToClassRefs(scanResult.getNamesOfAnnotationsOnClass(annotation)));
    }
}
