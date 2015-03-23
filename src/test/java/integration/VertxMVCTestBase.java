package integration;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class VertxMVCTestBase {
	protected Vertx vertx;

	@Before
	public void setUp(TestContext context) throws Exception {
		vertx = Vertx.vertx();
		vertx.deployVerticle("integration.TestVerticle", context.async().handler());
	}

	@After
	public void tearDown(TestContext context) throws Exception {
		if (vertx != null) {
			vertx.close(context.async().handler());
		}
	}
	
	public HttpClientOptions options(){
		HttpClientOptions options = new HttpClientOptions();
		options.setDefaultHost(TestVerticle.HOST);
		options.setDefaultPort(TestVerticle.PORT);
		return options;
	}
	
	public HttpClient client() {
		return vertx.createHttpClient(options());
	}

}
