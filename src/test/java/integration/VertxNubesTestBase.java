package integration;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.TestContext;
import mock.verticles.AnnotatedVerticle;

import org.junit.After;
import org.junit.Before;

public class VertxNubesTestBase {

	protected final static int NB_INSTANCES = 4;

	protected Vertx vertx;

	@Before
	public void setUp(TestContext context) throws Exception {
		vertx = Vertx.vertx();
		DeploymentOptions options = new DeploymentOptions();
		options.setInstances(NB_INSTANCES);
		vertx.deployVerticle("integration.TestVerticle", options, context.asyncAssertSuccess(handler -> {
			assertTrue(TestVerticle.dogService.size() > 0);
			assertEquals(NB_INSTANCES * 1, AnnotatedVerticle.nbInstances.get());
			assertTrue(AnnotatedVerticle.isStarted.get());
		}));
	}

	@After
	public void tearDown(TestContext context) throws Exception {
		if (vertx != null) {
			vertx.close(context.asyncAssertSuccess(handler -> {
				assertTrue(TestVerticle.dogService.isEmpty());
				assertFalse(AnnotatedVerticle.isStarted.get());
				AnnotatedVerticle.nbInstances.set(0);
			}));
		}
	}

	protected HttpClient client() {
		return vertx.createHttpClient(options());
	}

	protected void getJSON(String path, Handler<HttpClientResponse> responseHandler) {
		client().get(path, responseHandler).putHeader(ACCEPT, "application/json").end();
	}

	protected void sendJSON(String path, Object payload, Handler<HttpClientResponse> responseHandler) {
		client().post(path, responseHandler).putHeader(ACCEPT, "application/json").putHeader(CONTENT_TYPE, "application/json").end(payload.toString());
	}

	protected void getXML(String path, Handler<HttpClientResponse> responseHandler) {
		client().get(path, responseHandler).putHeader(ACCEPT, "application/xml").end();
	}

	protected void sendXML(String path, Object payload, Handler<HttpClientResponse> responseHandler) {
		client().post(path, responseHandler).putHeader(ACCEPT, "application/xml").putHeader(CONTENT_TYPE, "application/xml").end(payload.toString());
	}

	private HttpClientOptions options() {
		HttpClientOptions options = new HttpClientOptions();
		options.setDefaultHost(TestVerticle.HOST);
		options.setDefaultPort(TestVerticle.PORT);
		options.setKeepAlive(false);
		return options;
	}

}
