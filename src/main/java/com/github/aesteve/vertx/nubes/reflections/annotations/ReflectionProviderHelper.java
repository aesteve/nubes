package com.github.aesteve.vertx.nubes.reflections.annotations;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.reflections.annotations.impl.FastclassPathScannerProvider;
import com.github.aesteve.vertx.nubes.reflections.annotations.impl.ReflectionsAnnotationProvider;

/**
 * used to provide the correct scanner based on config options. currently defaults to Reflections implementation
 */
public class ReflectionProviderHelper {
    public static IAnnotationProvider getAnnotationProcessor(Config config, String packageName){
        if (config.getReflectionProvider().equals("reflections")){
            return new ReflectionsAnnotationProvider(packageName);
        } else {
            return new FastclassPathScannerProvider(packageName);

        }
    }
}
