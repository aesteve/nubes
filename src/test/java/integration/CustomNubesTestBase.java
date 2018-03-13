package integration;

import com.github.aesteve.vertx.nubes.VertxNubes;
import com.github.aesteve.vertx.nubes.utils.DateUtils;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import mock.custom.annotations.InjectCustomObject;
import mock.custom.annotations.InjectCustomObjectByName;
import mock.custom.annotations.SimpleAnnot;
import mock.custom.domains.CustomObject;
import mock.custom.handlers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Locale;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;


@RunWith(VertxUnitRunner.class)
public abstract class CustomNubesTestBase {

  protected final static String HOST = "localhost";
  protected final static int PORT = 8000;

  protected Vertx vertx;
  protected JsonObject config = new JsonObject();
  protected VertxNubes nubes;
  protected HttpServer server;

  private static HttpServerOptions serverOptions() {
    HttpServerOptions options = new HttpServerOptions();
    options.setHost(HOST);
    options.setPort(PORT);
    return options;
  }

  private static JsonObject createConfig() {
    JsonObject config = new JsonObject();
    config.put("src-package", "mock.custom");
    //config.put("reflectionProvider", "fastclasspathscanner");
    return config;
  }

  protected boolean hideErrors() {
    return false;
  }

  @Before
  public void setUp(TestContext context) throws Exception {
    Async async = context.async();
    vertx = Vertx.vertx();
    config = createConfig();
    nubes = new VertxNubes(vertx, config);
    nubes.registerAnnotationProcessor(InjectCustomObject.class, new InjectObjectProcessor());
    nubes.registerAnnotationProcessor(InjectCustomObjectByName.class, new InjectObjectByNameFactory());
    nubes.registerTypeParamInjector(CustomObject.class, new ResolveCustomObject());
    nubes.registerAnnotatedParamInjector(SimpleAnnot.class, new SimpleAnnotParamInjector());
    nubes.registerAdapter(CustomObject.class, new CustomObjectAdapter());
    nubes.setDefaultLocale(Locale.CANADA);
    nubes.setAvailableLocales(Arrays.asList(Locale.CANADA));
    nubes.addLocaleResolver(new CustomLocaleResolver());
    nubes.registerHandler(DateUtils.class, ctx -> {
      ctx.response().putHeader("X-Handler-Called", "true");
      ctx.next();
    });
    if (hideErrors()) {
      nubes.setFailureHandler(new MessageErrorHandler());
    }
    nubes.bootstrap(res -> {
      Router router = res.result();
      context.assertNotNull(router);
      server = vertx.createHttpServer(serverOptions());
      server.requestHandler(router::accept);
      server.listen(serverRes -> {
        context.assertFalse(serverRes.failed());
        async.complete();
      });
    });
  }

  @After
  public void tearDown(TestContext context) throws Exception {
    Async async = context.async();
    nubes.stop(res -> {
      context.assertFalse(res.failed());
      server.close(closeRes -> {
        context.assertFalse(closeRes.failed());
        async.complete();
      });
    });
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

  private HttpClientOptions options() {
    HttpClientOptions options = new HttpClientOptions();
    options.setDefaultHost(config.getString("host", HOST));
    options.setDefaultPort(config.getInteger("port", PORT));
    options.setKeepAlive(false);
    return options;
  }
}
