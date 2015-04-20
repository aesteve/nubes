package integration.params;

import static org.junit.Assert.*;
import mock.controllers.params.PathParametersTestController.Animal;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxNubesTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

@RunWith(VertxUnitRunner.class)
public class PathParametersTest extends VertxNubesTestBase {

    @Test
    public void testStringParam(TestContext context) {
    	String myString = "Snoopy";
        Async async = context.async();
        client().getNow("/params/path/string/"+myString, response -> {
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
    public void testLongParam(TestContext context) {
    	Long myLong = 1234l;
        Async async = context.async();
        client().getNow("/params/path/long/"+myLong, response -> {
            Buffer buff = Buffer.buffer();
            response.handler(buffer -> {
                buff.appendBuffer(buffer);
            });
            response.endHandler(handler -> {
                assertEquals(myLong.toString(), buff.toString("UTF-8"));
                async.complete();
            });
        });
    }
    
    @Test
    public void testIntParam(TestContext context) {
    	Integer myInt = 123;
        Async async = context.async();
        client().getNow("/params/path/int/"+myInt, response -> {
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
    public void testEnum(TestContext context) {
    	Animal animal = Animal.DOG;
        Async async = context.async();
        client().getNow("/params/path/enum/"+animal, response -> {
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

}
