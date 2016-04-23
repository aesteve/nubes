package mock.custom.handlers;

import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.NoopAfterAllProcessor;
import io.vertx.ext.web.RoutingContext;
import mock.custom.annotations.InjectCustomObject;
import mock.custom.domains.CustomObject;

public class InjectObjectProcessor extends NoopAfterAllProcessor implements AnnotationProcessor<InjectCustomObject> {

	public static final CustomObject obj = new CustomObject("some-name");
	
	@Override
	public void preHandle(RoutingContext context) {
		context.put(ResolveCustomObject.CTX_ID, obj);
		context.next();
	}

	@Override
	public void postHandle(RoutingContext context) {
		context.next();
	}

}
