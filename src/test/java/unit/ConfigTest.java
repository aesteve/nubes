package unit;

import com.github.aesteve.vertx.nubes.Config;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

  final static String SRC = "com.github.aesteve.nubes";

  @Test
  public void onlySrcPackage() {
    JsonObject json = new JsonObject();
    json.put("src-package", SRC);
    Config conf = Config.fromJsonObject(json, Vertx.vertx());
    assertEquals(Arrays.asList(SRC + ".controllers"), conf.getControllerPackages());
    assertEquals(SRC + ".verticles", conf.getVerticlePackage());
    assertEquals(Arrays.asList(SRC + ".fixtures"), conf.getFixturePackages());
    assertEquals(SRC + ".domains", conf.getDomainPackage());
  }


  @Test
  public void srcPackageAndCustomPaths() {
    JsonObject json = new JsonObject();
    JsonArray controllerPackages = new JsonArray().add("my.controllers");
    String verticlePackage = "my.verticles";
    String domainPackage = "my.domain";
    JsonArray fixturePackages = new JsonArray().add("my.fixtures");
    json.put("src-package", SRC);
    json.put("controller-packages", controllerPackages);
    json.put("verticle-package", verticlePackage);
    json.put("fixture-packages", fixturePackages);
    json.put("domain-package", domainPackage);
    Config conf = Config.fromJsonObject(json, Vertx.vertx());
    assertEquals(controllerPackages.getList(), conf.getControllerPackages());
    assertEquals(verticlePackage, conf.getVerticlePackage());
    assertEquals(fixturePackages.getList(), conf.getFixturePackages());
    assertEquals(domainPackage, conf.getDomainPackage());
  }

}
