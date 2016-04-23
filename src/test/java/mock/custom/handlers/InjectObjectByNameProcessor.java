package mock.custom.handlers;

import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.handlers.impl.NoopAfterAllProcessor;
import io.vertx.ext.web.RoutingContext;
import mock.custom.annotations.InjectCustomObjectByName;
import mock.custom.domains.CustomObject;

public class InjectObjectByNameProcessor extends NoopAfterAllProcessor implements AnnotationProcessor<InjectCustomObjectByName> {

	private String objectName;
	
	public InjectObjectByNameProcessor(InjectCustomObjectByName annot) {
		objectName = annot.value();
	}
	
	@Override
	public void preHandle(RoutingContext context) {
		context.put(ResolveCustomObject.CTX_ID, new CustomObject(objectName));
		context.next();
	}

	@Override
	public void postHandle(RoutingContext context) {
		context.next();
	}
}
