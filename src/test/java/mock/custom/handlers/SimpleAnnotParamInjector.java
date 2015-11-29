package mock.custom.handlers;

import com.github.aesteve.vertx.nubes.exceptions.params.WrongParameterException;
import com.github.aesteve.vertx.nubes.reflections.injectors.annot.AnnotatedParamInjector;

import io.vertx.ext.web.RoutingContext;
import mock.custom.annotations.SimpleAnnot;

public class SimpleAnnotParamInjector implements AnnotatedParamInjector<SimpleAnnot> {

	@Override
	public Object resolve(RoutingContext context, SimpleAnnot annotation, String paramName, Class<?> resultClass)
			throws WrongParameterException {
		return annotation.value();
	}


}
