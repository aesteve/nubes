package integration.views;

import static org.junit.Assert.assertEquals;
import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import mock.domains.Dog;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ViewsTest extends VertxNubesTestBase {

    // FIXME : some tests are just hanging out and doing nothing ?
    // While in the browser, everything works fine, even though we render the page 100 times...

    /*
     * @Test
     * public void testSimpleHtml(TestContext context) {
     * Async async = context.async();
     * client().getNow("/views/default", response -> {
     * assertEquals(200, response.statusCode());
     * response.bodyHandler(buffer -> {
     * String html = buffer.toString("UTF-8");
     * assertTrue(html.indexOf("<html>") == 0);
     * assertTrue(html.indexOf("</html>") > -1);
     * assertTrue(html.indexOf("Australia") > -1);
     * async.complete();
     * });
     * });
     * }
     */
    @Test
    public void testHandlebars(TestContext context) {
        Dog dog = TestVerticle.dogService.someDog();
        Async async = context.async();
        client().getNow("/views/handlebars?name=" + dog.getName() + "&breed=" + dog.getBreed(), response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                String result = buffer.toString("UTF-8");
                assertEquals("Hello! I'm a dog and my name is " + dog.getName(), result);
                async.complete();
            });
        });
    }
    /*
     * @Test
     * public void testMvel(TestContext context) {
     * Dog dog = DogFixture.someDog();
     * Async async = context.async();
     * client().getNow("/views/mvel?name="+dog.getName()+"&breed="+dog.getBreed(), response -> {
     * assertEquals(200, response.statusCode());
     * response.bodyHandler(buffer -> {
     * String result = buffer.toString("UTF-8");
     * assertEquals("Hello! I'm a dog and my name is "+dog.getName(), result);
     * async.complete();
     * });
     * });
     * }
     */
}
