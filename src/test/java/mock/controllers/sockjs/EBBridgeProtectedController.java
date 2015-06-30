package mock.controllers.sockjs;

import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.EventBusBridge;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.InboundPermitted;
import com.github.aesteve.vertx.nubes.annotations.sockjs.bridge.OutboundPermitted;

@EventBusBridge("/eventbus/regex/*")
@InboundPermitted(addressRegex = "inbound.authorized.*")
@OutboundPermitted(addressRegex = "outbound.authorized.*")
public class EBBridgeProtectedController {

}
