package unit;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.github.aesteve.vertx.nubes.Config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigTest {
	
	final static String SRC = "com.github.aesteve.nubes";
	
	@Test
	public void onlySrcPackage() {
		JsonObject json = new JsonObject();
		json.put("src-package", SRC);
		Config conf = Config.fromJsonObject(json, Vertx.vertx());
		assertEquals(Arrays.asList(SRC + ".controllers"), conf.controllerPackages);
		assertEquals(SRC + ".verticles", conf.verticlePackage);
		assertEquals(Arrays.asList(SRC + ".fixtures"), conf.fixturePackages);
		assertEquals(SRC + ".domains", conf.domainPackage);
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
		assertEquals(controllerPackages.getList(), conf.controllerPackages);
		assertEquals(verticlePackage, conf.verticlePackage);
		assertEquals(fixturePackages.getList(), conf.fixturePackages);
		assertEquals(domainPackage, conf.domainPackage);
	}
	
}
