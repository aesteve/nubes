package com.github.aesteve.vertx.nubes.reflections.factories.impl;

import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.FileProcessor;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;

public class FileProcessorFactory implements AnnotationProcessorFactory<File> {

  @Override
  public AnnotationProcessor<File> create(File annotation) {
    return new FileProcessor(annotation);
  }

}
