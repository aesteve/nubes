package integration.params;

import static org.junit.Assert.assertEquals;
import integration.VertxMVCTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.mvc.reflections.ParameterAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import mock.controllers.params.QueryParametersTestController.Animal;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class QueryParametersTest extends VertxMVCTestBase {
	
	@Test 
	public void mandatoryParam(TestContext context) {
        Async async = context.async();
        client().getNow("/params/query/string", response -> {
            assertEquals(400, response.statusCode());
            async.complete();
        });
	}
	
	@Test
	public void testString(TestContext context) {
    	String myString = "Snoopy";
        Async async = context.async();
        client().getNow("/params/query/string?parameter="+myString, response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(myString, buff.toString("UTF-8"));
                async.complete();
            });
        });
	}

	@Test
	public void testInt(TestContext context) {
    	Integer myInt = 123;
        Async async = context.async();
        client().getNow("/params/query/int?parameter="+myInt, response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(myInt.toString(), buff.toString("UTF-8"));
                async.complete();
            });
        });
	}

	@Test
	public void testLong(TestContext context) {
    	Long myInt = 1234l;
        Async async = context.async();
        client().getNow("/params/query/long?parameter="+myInt, response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(myInt.toString(), buff.toString("UTF-8"));
                async.complete();
            });
        });
	}

	@Test
	public void testFloat(TestContext context) {
    	Float myFloat = 123.45f;
        Async async = context.async();
        client().getNow("/params/query/float?parameter="+myFloat, response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(myFloat.toString(), buff.toString("UTF-8"));
                async.complete();
            });
        });
	}

	@Test
	public void testEnum(TestContext context) {
    	Animal animal = Animal.CAT;
        Async async = context.async();
        client().getNow("/params/query/enum?parameter="+animal, response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(animal.toString(), buff.toString("UTF-8"));
                async.complete();
            });
        });
	}

	@Test
	public void testDate(TestContext context) throws Exception {
    	Date date = new Date();
    	SimpleDateFormat format = ParameterAdapter.parser;
    	String iso = format.format(date);
        Async async = context.async();
        client().getNow("/params/query/date?parameter="+iso, response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(Long.toString(date.getTime()), buff.toString("UTF-8"));
                async.complete();
            });
        });
	}

}
