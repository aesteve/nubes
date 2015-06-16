package mock.controllers.file;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;
import com.github.aesteve.vertx.nubes.context.FileResolver;

import io.vertx.ext.web.RoutingContext;

@Controller("/file")
public class TestFileController {

    @Path("/txt")
    @File("someFile.txt")
    public void getTxtFile(RoutingContext context) {
        context.next();
    }

    @Path("/dynamic")
    @File
    public void getDynamicTxtFile(RoutingContext context) {
        FileResolver.resolve(context, "someOtherFile.txt");
        context.next();
    }

}
