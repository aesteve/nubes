package integration.params;

import static org.junit.Assert.assertEquals;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.nubes.reflections.adapters.impl.DefaultParameterAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(VertxUnitRunner.class)
public class HeadersParametersTest extends VertxNubesTestBase {

	private SimpleDateFormat parser;
	
	@Before
	public void setUp(TestContext context) throws Exception {
		this.parser = DefaultParameterAdapter.parser;
		super.setUp(context);
	}
	
	@Test
	public void nonMandatoryParamPresent(TestContext context) {
		Async async = context.async();
		Date now = new Date();
		client().get("/headers/facultative", response -> {
			assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				assertEquals(Long.toString(now.getTime()), buffer.toString("UTF-8"));
				async.complete();
			});
		}).putHeader("X-Date", parser.format(now)).end();;
	}

	@Test
	public void nonMandatoryParamAbsent(TestContext context) {
		Async async = context.async();
		client().get("/headers/facultative", response -> {
			assertEquals(200, response.statusCode());
			response.bodyHandler(buffer -> {
				assertEquals("null", buffer.toString("UTF-8"));
				async.complete();
			});
		}).end();
	}

	@Test
	public void mandatoryParamAbsent(TestContext context) {
		Async async = context.async();
		client().get("/headers/mandatory", response -> {
			assertEquals(400, response.statusCode());
			async.complete();
		}).end();
	}


}
