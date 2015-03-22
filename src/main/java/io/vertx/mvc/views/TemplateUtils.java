package io.vertx.mvc.views;

import io.vertx.ext.apex.templ.JadeTemplateEngine;
import io.vertx.ext.apex.templ.MVELTemplateEngine;
import io.vertx.ext.apex.templ.HandlebarsTemplateEngine;
import io.vertx.ext.apex.templ.TemplateEngine;
import io.vertx.ext.apex.templ.ThymeleafTemplateEngine;

public class TemplateUtils {
	// TODO : lazy initialize
	private static HandlebarsTemplateEngine defaultEngine = HandlebarsTemplateEngine.create(); // TODO : ask to know which should be the default one ?
	private static HandlebarsTemplateEngine hbsEngine = HandlebarsTemplateEngine.create();
	private static MVELTemplateEngine mvelEngine = MVELTemplateEngine.create();
	private static JadeTemplateEngine jadeEngine = JadeTemplateEngine.create();
	private static ThymeleafTemplateEngine thymeleafEngine = ThymeleafTemplateEngine.create();
	
	public static TemplateEngine fromName(String tplName) {
		int pointIdx = tplName.lastIndexOf(".");
		if (pointIdx == -1) {
			return defaultEngine;
		}
		String extension = tplName.substring(pointIdx, tplName.length());
		switch(extension) {
			case "hbs":
				return hbsEngine;
			case "mvel":
				return mvelEngine;
			case "jade":
				return jadeEngine;
			case "html": // FIXME : meh..
				return thymeleafEngine;
		}
		return defaultEngine; // FIXME : default to something ? 
	}
}
