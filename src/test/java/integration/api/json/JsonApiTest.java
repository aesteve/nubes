package integration.api.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import integration.TestVerticle;
import integration.VertxNubesTestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import static io.vertx.core.http.HttpHeaders.*;

import java.util.List;
import java.util.Map;

import mock.domains.Dog;

import org.junit.Test;
import org.junit.runner.RunWith;

// TODO : test errors

@RunWith(VertxUnitRunner.class)
public class JsonApiTest extends VertxNubesTestBase {

    @Test
    public void noContentType(TestContext context) {
        Async async = context.async();
        client().getNow("/json/dog", response -> {
            assertEquals(406, response.statusCode());
            async.complete();
        });
    }

    @Test
    public void wrongContentType(TestContext context) {
        Async async = context.async();
        client().get("/json/dog", response -> {
            assertEquals(406, response.statusCode());
            response.bodyHandler(buff -> {
                assertEquals("Not acceptable", buff.toString("UTF-8"));
                async.complete();
            });
        }).putHeader(ACCEPT, "yourmum").end();
    }

    @Test
    public void getMap(TestContext context) {
        Async async = context.async();
        getJSON("/json/map", response -> {
            assertEquals(200, response.statusCode());
            assertEquals(response.getHeader(CONTENT_TYPE.toString()), "application/json");
            response.handler(buffer -> {
                JsonObject json = new JsonObject(buffer.toString("UTF-8"));
                assertNotNull(json);
                assertEquals(json.getString("Snoopy"), "Beagle");
                assertEquals(json.getString("Bill"), "Cocker");
                async.complete();
            });
        });
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getArray(TestContext context) {
        Async async = context.async();
        getJSON("/json/array", response -> {
            assertEquals(200, response.statusCode());
            assertEquals(response.getHeader(CONTENT_TYPE.toString()), "application/json");
            response.handler(buffer -> {
                JsonArray json = new JsonArray(buffer.toString("UTF-8"));
                assertNotNull(json);
                List list = json.getList();
                assertEquals(list.get(0), "Snoopy");
                assertEquals(list.get(1), "Bill");
                async.complete();
            });
        });
    }

    @Test
    public void getDomainObject(TestContext context) {
        Async async = context.async();
        getJSON("/json/dog", response -> {
            assertEquals(200, response.statusCode());
            assertEquals(response.getHeader(CONTENT_TYPE.toString()), "application/json");
            response.handler(buffer -> {
                JsonObject json = new JsonObject(buffer.toString("UTF-8"));
                assertNotNull(json);
                assertEquals(json.getString("name"), "Snoopy");
                assertEquals(json.getString("breed"), "Beagle");
                async.complete();
            });
        });
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getDomainObjects(TestContext context) {
        Async async = context.async();
        getJSON("/json/dogs", response -> {
            assertEquals(200, response.statusCode());
            assertEquals("application/json", response.getHeader(CONTENT_TYPE.toString()));
            response.handler(buffer -> {
                JsonArray json = new JsonArray(buffer.toString("UTF-8"));
                assertNotNull(json);
                List list = json.getList();
                assertEquals(((Map) list.get(0)).get("name"), "Snoopy");
                assertEquals(((Map) list.get(0)).get("breed"), "Beagle");
                assertEquals(((Map) list.get(1)).get("name"), "Bill");
                assertEquals(((Map) list.get(1)).get("breed"), "Cocker");
                async.complete();
            });
        });
    }

    @Test
    public void postSomeStuff(TestContext context) {
        Dog dog = TestVerticle.dogService.someDog();
        JsonObject dogJson = new JsonObject();
        dogJson.put("name", dog.getName());
        dogJson.put("breed", dog.getBreed());
        Async async = context.async();
        sendJSON("/json/postdog", dogJson, response -> {
            assertEquals(200, response.statusCode());
            assertEquals("application/json", response.getHeader(CONTENT_TYPE.toString()));
            response.bodyHandler(buffer -> {
                String json = buffer.toString("UTF-8");
                JsonObject receivedDog = new JsonObject(json);
                assertEquals(dog.getName(), receivedDog.getString("name"));
                assertEquals(dog.getBreed(), receivedDog.getString("breed"));
                async.complete();
            });
        });
    }

}
