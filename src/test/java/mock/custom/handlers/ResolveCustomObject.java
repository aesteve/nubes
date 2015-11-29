package mock.custom.handlers;

import com.github.aesteve.vertx.nubes.reflections.injectors.typed.ParamInjector;

import io.vertx.ext.web.RoutingContext;
import mock.custom.domains.CustomObject;

public class ResolveCustomObject implements ParamInjector<CustomObject>{

	public static final String CTX_ID = "custom-object";

	
	@Override
	public CustomObject resolve(RoutingContext context) {
		return context.get(CTX_ID);
	}

}
