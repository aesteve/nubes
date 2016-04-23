package mock.custom.handlers;

import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;
import com.github.aesteve.vertx.nubes.reflections.factories.AnnotationProcessorFactory;
import mock.custom.annotations.InjectCustomObjectByName;

public class InjectObjectByNameFactory implements AnnotationProcessorFactory<InjectCustomObjectByName> {

	@Override
	public AnnotationProcessor<InjectCustomObjectByName> create(InjectCustomObjectByName annotation) {
		return new InjectObjectByNameProcessor(annotation);
	}

}
