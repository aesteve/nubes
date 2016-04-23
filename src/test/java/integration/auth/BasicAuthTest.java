package integration.auth;

import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.controllers.auth.RedirectedController;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static io.vertx.core.http.HttpHeaders.*;

public class BasicAuthTest extends VertxNubesTestBase {

  protected AtomicReference<String> sessionCookie = new AtomicReference<>();

  private static String goodUsername() {
    return "tim";
  }

  private static String goodPwd() {
    return "sausages";
  }

  private static String getOKBearer() {
    return "Basic dGltOnNhdXNhZ2Vz";
  }

  private static String getWrongBearer() {
    return "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
  }

  @Test
  public void test401(TestContext context) {
    Async async = context.async();
    client().getNow("/private/user", response -> {
      context.assertEquals(401, response.statusCode());
      async.complete();
    });
  }

  @Test
  public void testAuthorized(TestContext context) {
    Async async = context.async();
    client().get("/private/user", response -> {
      context.assertEquals(200, response.statusCode());
      async.complete();
    }).putHeader(AUTHORIZATION, getOKBearer()).end();
  }

  @Test
  public void testForbidden(TestContext context) {
    Async async = context.async();
    client().get("/private/admin", response -> {
      context.assertEquals(403, response.statusCode());
      async.complete();
    }).putHeader(AUTHORIZATION, getWrongBearer()).end();
  }

  @Test
  public void testRedirect(TestContext context) {
    Async async = context.async();
    String redirectURL = RedirectedController.REDIRECT_URL;
    String originalURL = "/auth/redirected/private";
    client().getNow(originalURL, response -> {
      context.assertEquals(302, response.statusCode());
      String setCookie = response.headers().get(SET_COOKIE);
      context.assertNotNull(setCookie);
      sessionCookie.set(setCookie);
      context.assertEquals(redirectURL, response.headers().get(LOCATION));
      client().getNow(redirectURL, loginPageResponse -> {
        context.assertEquals(200, loginPageResponse.statusCode());
        HttpClientRequest loginRequest = client().post(redirectURL, loginResponse -> {
          context.assertEquals(302, loginResponse.statusCode());
          context.assertEquals(loginResponse.headers().get(LOCATION), originalURL);
          async.complete();
        });
        String boundary = "dLV9Wyq26L_-JQxk6ferf-RT153LhOO";
        Buffer buffer = Buffer.buffer();
        String str = "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"username\"\r\n\r\n" + goodUsername() + "\r\n" +
            "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"password\"\r\n\r\n" + goodPwd() + "\r\n" +
            "--" + boundary + "--\r\n";
        buffer.appendString(str);
        loginRequest.putHeader("content-length", String.valueOf(buffer.length()));
        loginRequest.putHeader("content-type", "multipart/form-data; boundary=" + boundary);
        if (sessionCookie.get() != null) {
          loginRequest.putHeader(COOKIE, sessionCookie.get());
        }
        loginRequest.write(buffer);
        loginRequest.end();
      });
    });
  }
}
