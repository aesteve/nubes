package integration.file;

import static org.junit.Assert.assertEquals;
import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class TestFile extends VertxNubesTestBase {

	@Test
	public void getFile(TestContext context) {
		Async async = context.async();
		client().getNow("/file/txt", response -> {
			response.bodyHandler(buff -> {
				assertEquals("This is a text file", buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void getFileDynamic(TestContext context) {
		Async async = context.async();
		client().getNow("/file/dynamic", response -> {
			response.bodyHandler(buff -> {
				assertEquals("This is another text file", buff.toString("UTF-8"));
				async.complete();
			});
		});
	}

}
