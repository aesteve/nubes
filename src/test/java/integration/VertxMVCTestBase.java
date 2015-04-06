package integration;

import static org.junit.Assert.assertTrue;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.unit.TestContext;
import mock.fixtures.DogFixture;

import org.junit.After;
import org.junit.Before;

public class VertxMVCTestBase {
    protected Vertx vertx;

    @Before
    public void setUp(TestContext context) throws Exception {
    	vertx = Vertx.vertx();
        vertx.deployVerticle("integration.TestVerticle", context.asyncAssertSuccess(handler -> {
        	assertTrue(DogFixture.dogs.size() > 0);
        }));
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        if (vertx != null) {
            vertx.close(context.asyncAssertSuccess(handler -> {
            	assertTrue(DogFixture.dogs.isEmpty());
            }));
        }
    }

    public HttpClientOptions options() {
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultHost(TestVerticle.HOST);
        options.setDefaultPort(TestVerticle.PORT);
        options.setKeepAlive(false);
        return options;
    }

    public HttpClient client() {
        return vertx.createHttpClient(options());
    }

}
