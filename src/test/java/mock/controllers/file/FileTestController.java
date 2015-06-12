package mock.controllers.file;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.routing.Path;

import io.vertx.ext.web.RoutingContext;

@Controller("/file")
public class FileTestController {

    @Path("/txt")
    @File("someFile.txt")
    public void getTxtFile(RoutingContext context) {
        context.next();
    }

}
