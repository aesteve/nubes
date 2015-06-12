package integration.api.xml;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.Assert.assertEquals;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

// TODO : test errors

@RunWith(VertxUnitRunner.class)
public class XmlApiTest extends VertxNubesTestBase {

    private final static String dogXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><dog><breed>Beagle</breed><name>Snoopy</name></dog>";

    @Test
    public void noContentType(TestContext context) {
        Async async = context.async();
        client().getNow("/xml/dog", response -> {
            assertEquals(406, response.statusCode());
            async.complete();
        });
    }

    @Test
    public void wrongContentType(TestContext context) {
        Async async = context.async();
        client().get("/xml/dog", response -> {
            assertEquals(406, response.statusCode());
            response.bodyHandler(buff -> {
                assertEquals("Not acceptable", buff.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT, "yourmum").end();
    }

    @Test
    public void getDomainObject(TestContext context) {
        Async async = context.async();
        getJSON("/xml/dog", response -> {
            assertEquals(200, response.statusCode());
            assertEquals(response.getHeader(CONTENT_TYPE.toString()), "application/xml");
            response.handler(buffer -> {
                assertEquals(dogXML, buffer.toString("UTF-8"));
                async.complete();
            });
        });
    }

    @Test
    public void postSomeStuff(TestContext context) {
        Async async = context.async();
        sendJSON("/xml/postdog", dogXML, response -> {
            assertEquals(200, response.statusCode());
            assertEquals("application/xml", response.getHeader(CONTENT_TYPE.toString()));
            response.bodyHandler(buffer -> {
                assertEquals(dogXML, buffer.toString("UTF-8"));
                async.complete();
            });
        });
    }

}
