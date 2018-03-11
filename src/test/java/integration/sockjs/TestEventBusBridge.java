package integration.sockjs;

import io.vertx.core.http.WebSocketBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestEventBusBridge extends EventBusBridgeTestBase {

  private final static String TEST_EB_ADDRESS = "test.address";

  @Test
  public void testSendRelatedEvents() throws Exception {
    CountDownLatch latch = new CountDownLatch(3);
    String msg = "Happiness is a warm puppy";
    vertx.eventBus().consumer(TEST_EB_ADDRESS, ebMsg -> {
      assertEquals(msg, ebMsg.body());
      latch.countDown();
    });

    vertx.eventBus().consumer(BridgeEventType.SOCKET_CREATED.toString(), ebMsg -> {
      assertEquals(BridgeEventType.SOCKET_CREATED.toString(), ebMsg.body());
      latch.countDown();
    });
    vertx.eventBus().consumer(BridgeEventType.SEND.toString(), ebMsg -> {
      assertEquals(BridgeEventType.SEND.toString(), ebMsg.body());
      latch.countDown();
    });
    client().websocket("/eventbus/default/websocket", ws -> {
      sendThroughBridge(ws, TEST_EB_ADDRESS, msg);
    });
    assertTrue(latch.await(2, TimeUnit.SECONDS));
  }

  @Test
  public void testPublishEvent(TestContext context) throws Exception {
    Async async = context.async();
    String msg = "Happiness is a warm puppy";
    vertx.eventBus().consumer(BridgeEventType.PUBLISH.toString(), ebMsg -> {
      context.assertEquals(BridgeEventType.PUBLISH.toString(), ebMsg.body());
      async.complete();
    });
    client().websocket("/eventbus/default/websocket", ws -> {
      publishThroughBridge(ws, TEST_EB_ADDRESS, msg);
    });
  }

  @Test
  @Ignore
  public void testRegisterThenReceive() throws Exception {
    CountDownLatch latch = new CountDownLatch(3);
    String msg = "Happiness is a warm puppy";
    vertx.eventBus().consumer(BridgeEventType.REGISTER.toString(), ebMsg -> {
      assertEquals(BridgeEventType.REGISTER.toString(), ebMsg.body());
      latch.countDown();
    });
    vertx.eventBus().consumer(BridgeEventType.RECEIVE.toString(), ebMsg -> {
      assertEquals(BridgeEventType.RECEIVE.toString(), ebMsg.body());
      latch.countDown();
    });
    client().websocket("/eventbus/default/websocket", ws -> {
      registerThroughBridge(ws, TEST_EB_ADDRESS, msg);
      ws.handler(buff -> {
        JsonObject json = new JsonObject(buff.toString("UTF-8"));
        assertEquals(msg, json.getString("body"));
        latch.countDown();
      });

      try {
        Thread.sleep(1000);/* let some time to handle the register event on server side */
      } catch (Exception e) {
      }
      vertx.eventBus().publish(TEST_EB_ADDRESS, msg);
    });
    assertTrue(latch.await(3, TimeUnit.SECONDS));
  }

  @Test
  public void testCloseEvent(TestContext context) throws Exception {
    Async async = context.async();
    vertx.eventBus().consumer(BridgeEventType.SOCKET_CLOSED.toString(), ebMsg -> {
      context.assertEquals(BridgeEventType.SOCKET_CLOSED.toString(), ebMsg.body());
      async.complete();
    });
    client().websocket("/eventbus/default/websocket", WebSocketBase::close);
  }
}
