package integration;

import com.github.aesteve.vertx.nubes.NubesServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import mock.verticles.AnnotatedVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@RunWith(VertxUnitRunner.class)
public abstract class NubesServerTestBase {

  protected final static int NB_INSTANCES = 4; // to make sure it works well in a multiple-instance environment
  protected final static String HOST = "localhost";
  protected final static int PORT = 8000;

  protected Vertx vertx;
  private JsonObject config = new JsonObject();

  private static JsonObject createConfig() {
    JsonObject config = new JsonObject();
    config.put("host", HOST);
    config.put("port", PORT);
    config.put("src-package", "mock");
    //config.put("relectionprovider", "reflections");
    JsonObject services = new JsonObject();
    services.put("dogService", "mock.services.DogService");
    config.put("services", services);
    JsonObject throttling = new JsonObject();
    throttling.put("time-frame", 10);
    throttling.put("time-unit", TimeUnit.SECONDS.toString());
    throttling.put("count", 2); // 2 request per 10 seconds
    config.put("throttling", throttling);
    return config;
  }

  @Before
  public void setUp(TestContext context) throws Exception {
    vertx = Vertx.vertx();
    DeploymentOptions options = new DeploymentOptions();
    options.setInstances(NB_INSTANCES);
    config = createConfig();
    options.setConfig(config);
    vertx.deployVerticle(NubesServer.class.getName(), options, context.asyncAssertSuccess(handler -> {
      context.assertEquals(NB_INSTANCES, AnnotatedVerticle.nbInstances.get());
      context.assertTrue(AnnotatedVerticle.isStarted.get());
    }));
  }

  @After
  public void tearDown(TestContext context) throws Exception {
    if (vertx != null) {
      vertx.close(context.asyncAssertSuccess(handler -> {
        context.assertFalse(AnnotatedVerticle.isStarted.get());
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
    options.setDefaultHost(config.getString("host", HOST));
    options.setDefaultPort(config.getInteger("port", PORT));
    options.setKeepAlive(false);
    return options;
  }

}
