package integration.sockjs;

import io.vertx.ext.unit.TestContext;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestProtectedEventBusBridge extends EventBusBridgeTestBase {

  public final static String INBOUND_PERM_1 = "inboundPermitted1";
  public final static String INBOUND_PERM_2 = "inboundPermitted2";
  public final static String OUTBOUND_PERM_1 = "outboundPermitted1";
  public final static String OUTBOUND_PERM_2 = "outboundPermitted2";

  private final static String WS_ADDRESS = "/eventbus/protected/websocket";

  @Test
  public void testInboundPermitted1(TestContext context) {
    testInboundPermitted(context, WS_ADDRESS, INBOUND_PERM_1);
  }

  @Test
  public void testInboundPermitted2(TestContext context) {
    testInboundPermitted(context, WS_ADDRESS, INBOUND_PERM_2);
  }

  @Test
  public void testOutboundPermitted1(TestContext context) {
    testOutboundPermitted(context, WS_ADDRESS, OUTBOUND_PERM_1);
  }

  @Test
  public void testOutboundPermitted2(TestContext context) {
    testOutboundPermitted(context, WS_ADDRESS, OUTBOUND_PERM_2);
  }

  @Test
  public void testInboundRefused(TestContext context) {
    String address = "notallowed";
    testInboundRefused(context, WS_ADDRESS, address);

  }

  @Test
  public void testOutboundRefused(TestContext context) {
    String address = "notallowed";
    testOutboundRefused(context, WS_ADDRESS, address);
  }

}
