package integration.services;

import static org.junit.Assert.assertEquals;
import integration.VertxNubesTestBase;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class TestService extends VertxNubesTestBase {

	@Test
	public void testPeriodic(TestContext context) {
		Async async = context.async();
		vertx.eventBus().consumer("dogService.periodic", message -> {
			assertEquals("periodic", message.body());
			async.complete();
		});
	}

	@Test
	public void testEventBus(TestContext context) {
		Async async = context.async();
		String msg = "test";
		vertx.eventBus().send("dogService.echo", msg, res -> {
			Message<Object> reply = res.result();
			assertEquals(msg, reply.body());
			async.complete();
		});

	}
}
