package integration.web;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import integration.VertxMVCTestBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.unit.TestContext;

/**
 * @README : Be careful : runnin this test from your IDE won't work
 * Since the assets path is set as a relative path ("web/assets/") it can only work when ran from the root of the project
 */
@RunWith(VertxUnitRunner.class)
public class AssetsTest extends VertxMVCTestBase {
	@Test
	public void txtFile(TestContext context) throws Exception {
		Async async = context.async();
		client().getNow("/assets/hello.txt", response -> {
			assertEquals(200, response.statusCode());
			Buffer buff = Buffer.buffer();
			response.handler(buffer -> {
				buff.appendBuffer(buffer);
			});
			response.endHandler( handler -> {
				assertEquals("It was a dark stormy night...", buff.toString("UTF-8"));
				async.complete();
			});
		});
	}
}
