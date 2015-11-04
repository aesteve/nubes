package mock.controllers.file;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.context.FileResolver;

@Controller("/file")
public class TestFileController {

	@GET("/txt")
	@File("someFile.txt")
	public void getTxtFile() {}

	@GET("/dynamic")
	@File
	public void getDynamicTxtFile(RoutingContext context) {
		FileResolver.resolve(context, "someOtherFile.txt");
		context.next();
	}

	@GET("/dynamic/sync")
	@File
	public String getDynamicSync() {
		return "yetAnotherFile.txt";
	}

}
