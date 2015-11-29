package mock.custom.handlers;

import java.util.List;
import java.util.Locale;

import com.github.aesteve.vertx.nubes.i18n.LocaleResolver;

import io.vertx.ext.web.RoutingContext;

public class CustomLocaleResolver implements LocaleResolver {

	public static Locale LOCALE = Locale.TRADITIONAL_CHINESE; 
	
	@Override
	public Locale resolve(RoutingContext context, List<Locale> availableLocales) {
		return LOCALE;
	}

}
