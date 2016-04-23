package mock.custom.handlers;

import com.github.aesteve.vertx.nubes.reflections.adapters.ParameterAdapter;
import io.vertx.core.MultiMap;
import mock.custom.domains.CustomObject;

public class CustomObjectAdapter implements ParameterAdapter<CustomObject> {

	@Override
	public CustomObject adaptParam(String value, Class<? extends CustomObject> parameterClass) {
		return new CustomObject(value);
	}

	@Override
	public CustomObject adaptParams(MultiMap map, Class<? extends CustomObject> parameterClass) {
		return null;
	}

}
