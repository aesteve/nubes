package integration.sockjs;

import io.vertx.ext.unit.TestContext;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestRegexEventBusBridge extends EventBusBridgeTestBase {

	private final static String WS_ADDRESS = "/eventbus/regex/websocket";

	@Test
	public void testInboundPermitted(TestContext context) {
		testInboundPermitted(context, WS_ADDRESS, "inbound.authorized.something");
	}

	@Test
	public void testOutboundPermitted(TestContext context) {
		testOutboundPermitted(context, WS_ADDRESS, "outbound.authorized.something");
	}

	@Test
	public void testInboundRefused(TestContext context) {
		testInboundRefused(context, WS_ADDRESS, "inbound.refused.something");
	}

	@Test
	public void testOutboundRefused(TestContext context) {
		testOutboundRefused(context, WS_ADDRESS, "outbound.refused.something");
	}

}
