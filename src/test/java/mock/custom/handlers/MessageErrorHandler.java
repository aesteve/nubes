package mock.custom.handlers;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ErrorHandler;

public class MessageErrorHandler implements ErrorHandler {

	@Override
	public void handle(RoutingContext ctx) {
		HttpServerResponse response = ctx.response();
		response.setStatusCode(500);
		response.end(ctx.failure().getMessage());
	}

}
