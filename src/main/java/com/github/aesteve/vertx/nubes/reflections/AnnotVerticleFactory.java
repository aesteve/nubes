package com.github.aesteve.vertx.nubes.reflections;

import com.github.aesteve.vertx.nubes.Config;
import com.github.aesteve.vertx.nubes.annotations.services.Verticle;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;
import com.github.aesteve.vertx.nubes.reflections.annotations.ReflectionProviderHelper;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotVerticleFactory {

  private static final Logger LOG = LoggerFactory.getLogger(AnnotVerticleFactory.class);

  private final Config config;

  public AnnotVerticleFactory(Config config) {
    this.config = config;
  }

  public Map<String, DeploymentOptions> scan() {
    Map<String, DeploymentOptions> map = new HashMap<>();
    String verticlePackage = config.getVerticlePackage();
    if (verticlePackage == null) {
      return map;
    }

    Set<Class<?>> classes = ReflectionProviderHelper.getAnnotationProcessor(config, verticlePackage).getClassesTypesAnnotatedWith(Verticle.class);
    classes.forEach(clazz -> {
      if (!io.vertx.core.Verticle.class.isAssignableFrom(clazz)) {
        LOG.error("Cannot create verticle " + clazz.getName() + " since it's not a subclass of io.vertx.core.Verticle");
      } else {
        Verticle annot = clazz.getAnnotation(Verticle.class);
        map.put(clazz.getName(), getDeploymentOptions(annot));
      }
    });
    return map;
  }

  private DeploymentOptions getDeploymentOptions(Verticle annot) {
    DeploymentOptions options = new DeploymentOptions();
    if (annot.inheritsConfig()) {
      options.setConfig(config.json());
    }
    if (annot.instances() > 0) {
      options.setInstances(annot.instances());
    }
    if (!"".equals(annot.isolationGroup())) {
      options.setIsolationGroup(annot.isolationGroup());
    }
    options.setHa(annot.ha());
    options.setMultiThreaded(annot.multiThreaded());
    options.setWorker(annot.worker());
    return options;
  }
}
