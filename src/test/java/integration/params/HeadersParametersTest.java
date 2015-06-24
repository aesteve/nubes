package integration.params;

import static org.junit.Assert.assertEquals;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.github.aesteve.vertx.nubes.utils.DateUtils;

public class HeadersParametersTest extends VertxNubesTestBase {

    @Before
    public void setUp(TestContext context) throws Exception {
        super.setUp(context);
    }

    @Test
    public void nonMandatoryParamPresent(TestContext context) {
        Async async = context.async();
        Date now = new Date();
        client().get("/headers/facultative", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals(Long.toString(now.getTime()), buffer.toString("UTF-8"));
                async.complete();
            });
        }).putHeader("X-Date", DateUtils.INSTANCE.formatIso8601(now)).end();
    }

    @Test
    public void nonMandatoryParamAbsent(TestContext context) {
        Async async = context.async();
        client().get("/headers/facultative", response -> {
            assertEquals(200, response.statusCode());
            response.bodyHandler(buffer -> {
                assertEquals("null", buffer.toString("UTF-8"));
                async.complete();
            });
        }).end();
    }

    @Test
    public void mandatoryParamAbsent(TestContext context) {
        Async async = context.async();
        client().get("/headers/mandatory", response -> {
            assertEquals(400, response.statusCode());
            async.complete();
        }).end();
    }

}
