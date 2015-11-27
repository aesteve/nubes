package integration.params;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.github.aesteve.vertx.nubes.utils.DateUtils;

public class HeadersParametersTest extends VertxNubesTestBase {

	@Before
	public void setUp(TestContext context) throws Exception {
		super.setUp(context);
	}

	@Test
	public void nonMandatoryParamPresent(TestContext context) {
		Async async = context.async();
		Date now = new Date();
		client().get("/headers/facultative", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals(Long.toString(now.getTime()), buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader("X-Date", DateUtils.INSTANCE.formatIso8601(now)).end();
	}

	@Test
	public void nonMandatoryParamAbsent(TestContext context) {
		Async async = context.async();
		client().get("/headers/facultative", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals("null", buffer.toString("UTF-8"));
				async.complete();
			});
		}).end();
	}

	@Test
	public void mandatoryParamAbsent(TestContext context) {
		Async async = context.async();
		client().get("/headers/mandatory", response -> {
			context.assertEquals(400, response.statusCode());
			response.bodyHandler(buff -> {
				String msg = buff.toString("UTF-8");
				context.assertTrue(msg.contains("mandatory"));
				async.complete();
			});
		}).end();
	}

	@Test
	public void echoByName(TestContext context) {
		Async async = context.async();
		String value = "Pluto";
		client().get("/headers/echoByName", response -> {
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				context.assertEquals(value, buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader("someHeader", value).end();
	}

	@Test
	public void wrongHeaderType(TestContext context) {
		Async async = context.async();
		String header = "X-Date";
		String value = "invalidDate";
		client().get("/headers/facultative", response -> {
			context.assertEquals(400, response.statusCode());
			response.bodyHandler(buffer -> {
				System.out.println(buffer.toString("UTF-8"));
				context.assertEquals("Invalid value : " + value + " for header : " + header, buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(header, value).end();

	}

	@Test
	public void wrongHeadersType(TestContext context) {
		Async async = context.async();
		String header = "X-Date";
		String value = "invalidDate";
		client().get("/headers/wrongHeaders", response -> {
			context.assertEquals(500, response.statusCode());
			async.complete();
		}).putHeader(header, value).end();

	}

}
