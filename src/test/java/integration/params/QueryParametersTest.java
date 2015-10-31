package integration.params;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import java.util.Date;

import mock.controllers.params.QueryParametersTestController.Animal;

import org.junit.Test;

import com.github.aesteve.vertx.nubes.utils.DateUtils;

public class QueryParametersTest extends VertxNubesTestBase {

	@Test
	public void mandatoryParam(TestContext context) {
		Async async = context.async();
		client().getNow("/params/query/string", response -> {
			context.assertEquals(400, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testString(TestContext context) {
		String myString = "Snoopy";
		Async async = context.async();
		client().getNow("/params/query/string?parameter=" + myString, response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(myString, buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void testInt(TestContext context) {
		Integer myInt = 123;
		Async async = context.async();
		client().getNow("/params/query/int?parameter=" + myInt, response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(myInt.toString(), buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void testLong(TestContext context) {
		Long myInt = 1234l;
		Async async = context.async();
		client().getNow("/params/query/long?parameter=" + myInt, response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(myInt.toString(), buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void testFloat(TestContext context) {
		Float myFloat = 123.45f;
		Async async = context.async();
		client().getNow("/params/query/float?parameter=" + myFloat, response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(myFloat.toString(), buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void testEnum(TestContext context) {
		Animal animal = Animal.CAT;
		Async async = context.async();
		client().getNow("/params/query/enum?parameter=" + animal, response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(animal.toString(), buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void testDate(TestContext context) throws Exception {
		Date date = new Date();
		String iso = DateUtils.INSTANCE.formatIso8601(date);
		Async async = context.async();
		client().getNow("/params/query/date?parameter=" + iso, response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(Long.toString(date.getTime()), buff.toString("UTF-8"));
				async.complete();
			});
		});
	}
	
	@Test
	public void testParamByName(TestContext context) throws Exception {
		String name = "Snoopy";
		Async async = context.async();
		client().getNow("/params/query/byName?dog="+name, response -> {
			response.bodyHandler(buff -> {
				context.assertEquals(name, buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

}
