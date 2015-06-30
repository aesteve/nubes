package mock.controllers.sockjs;

import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.EventBusBridge;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.InboundPermitted;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.OutboundPermitted;
import static integration.sockjs.TestProtectedEventBusBridge.*;

@EventBusBridge("/eventbus/protected/*")
@InboundPermitted(address = INBOUND_PERM_1)
@InboundPermitted(address = INBOUND_PERM_2)
@OutboundPermitted(address = OUTBOUND_PERM_1)
@OutboundPermitted(address = OUTBOUND_PERM_2)
public class EBBridgeRegexProtectedController {

}
