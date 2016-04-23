package integration.custom;

import integration.CustomNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import mock.custom.handlers.CustomLocaleResolver;
import org.junit.Test;

public class LocaleResolverTest extends CustomNubesTestBase {
  @Test
  public void testLocaleResolver(TestContext context) {
    Async async = context.async();
    client().getNow("/custom/locale", response -> {
      context.assertEquals(200, response.statusCode());
      response.bodyHandler(buff -> {
        String loc = buff.toString("UTF-8");
        context.assertEquals(CustomLocaleResolver.LOCALE.toLanguageTag(), loc);
        async.complete();
      });
    });
  }
}
