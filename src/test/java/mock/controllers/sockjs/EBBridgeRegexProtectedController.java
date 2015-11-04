package mock.controllers.sockjs;

import static integration.sockjs.TestProtectedEventBusBridge.INBOUND_PERM_1;
import static integration.sockjs.TestProtectedEventBusBridge.INBOUND_PERM_2;
import static integration.sockjs.TestProtectedEventBusBridge.OUTBOUND_PERM_1;
import static integration.sockjs.TestProtectedEventBusBridge.OUTBOUND_PERM_2;

import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.EventBusBridge;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.InboundPermitted;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.OutboundPermitted;

@EventBusBridge("/eventbus/protected/*")
@InboundPermitted(address = INBOUND_PERM_1)
@InboundPermitted(address = INBOUND_PERM_2)
@OutboundPermitted(address = OUTBOUND_PERM_1)
@OutboundPermitted(address = OUTBOUND_PERM_2)
public class EBBridgeRegexProtectedController {

}
