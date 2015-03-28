package integration;

import static org.junit.Assert.assertTrue;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.fixtures.DogFixture;

import org.junit.After;
import org.junit.Before;

public class VertxMVCTestBase {
    protected Vertx vertx;

    @Before
    public void setUp(TestContext context) throws Exception {
    	vertx = Vertx.vertx();
    	Async async = context.async();
        vertx.deployVerticle("integration.TestVerticle", handler -> {
        	if (handler.cause() != null) {
        		handler.cause().printStackTrace();
        	}
        	assertTrue(handler.succeeded());
        	assertTrue(DogFixture.dogs.size() > 0);
        	async.complete();
        });
    }

    @After
    public void tearDown(TestContext context) throws Exception {
    	Async async = context.async();
        if (vertx != null) {
            vertx.close(handler -> {
            	if (handler.cause() != null) {
            		handler.cause().printStackTrace();
            	}
            	assertTrue(handler.succeeded());
            	assertTrue(DogFixture.dogs.isEmpty());
            	async.complete();
            });
        }
    }

    public HttpClientOptions options() {
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultHost(TestVerticle.HOST);
        options.setDefaultPort(TestVerticle.PORT);
        return options;
    }

    public HttpClient client() {
        return vertx.createHttpClient(options());
    }

}
