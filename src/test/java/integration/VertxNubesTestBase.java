package integration;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import mock.verticles.AnnotatedVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@RunWith(VertxUnitRunner.class)
public abstract class VertxNubesTestBase {

  protected final static int NB_INSTANCES = 4; // to make sure it works well in a multiple-instance environment

  protected Vertx vertx;

  private static HttpClientOptions options() {
    HttpClientOptions options = new HttpClientOptions();
    options.setDefaultHost(TestVerticle.HOST);
    options.setDefaultPort(TestVerticle.PORT);
    options.setKeepAlive(false);
    return options;
  }

  protected JsonArray getControllerPackages() {
    return new JsonArray().add("mock.controllers");
  }

  @Before
  public void setUp(TestContext context) throws Exception {
    vertx = Vertx.vertx();
    DeploymentOptions options = new DeploymentOptions();
    options.setInstances(NB_INSTANCES);
    options.setConfig(new JsonObject().put("controller-packages", getControllerPackages()));
    vertx.deployVerticle("integration.TestVerticle", options, context.asyncAssertSuccess(handler -> {
      context.assertTrue(TestVerticle.dogService.size() > 0);
      context.assertEquals(NB_INSTANCES, AnnotatedVerticle.nbInstances.get());
      context.assertTrue(AnnotatedVerticle.isStarted.get());
    }));
  }

  @After
  public void tearDown(TestContext context) throws Exception {
    if (vertx != null) {
      vertx.close(context.asyncAssertSuccess(handler -> {
        context.assertTrue(TestVerticle.dogService.isEmpty());
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

}
