package mock.controllers.paginationcontext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.context.PaginationContext;

import io.vertx.core.json.JsonObject;

@Controller("/paginationcontext")
@ContentType("application/json")
public class PaginationContextTestController {

	@GET
	public JsonObject echoPaginationContext(PaginationContext ctx) {
		ctx.setNbItems(0);
		return ctx.toJsonObject();
	}

	@GET("/more")
	public JsonObject moreThanOnePage(PaginationContext ctx) {
		ctx.setNbItems(302);
		return ctx.toJsonObject();
	}
}
