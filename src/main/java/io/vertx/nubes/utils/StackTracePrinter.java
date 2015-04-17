package io.vertx.nubes.utils;

public class StackTracePrinter {
	
	public static StringBuilder asHtml(StringBuilder sb, Throwable error) {
		if (sb == null) {
			sb = new StringBuilder();
		}
		sb.append("<div class=\"exception\">");
		sb.append("<div class=\"exception-msg\">" + error.getMessage() + "</div>");
		sb.append("<ul class=\"stacktrace\">");
		for (StackTraceElement ste : error.getStackTrace()) {
			sb.append("<li>" + ste.toString() + "</li>");
		}
		sb.append("</ul>");
		sb.append("</div>");
		if (error.getCause() != null) {
			sb.append("<div class=\"caused-by\">Caused by : </div>");
			asHtml(sb, error.getCause());
		}
		return sb;
	}
	
	public static StringBuilder asLineString(StringBuilder sb, Throwable error) {
		if (sb == null) {
			sb = new StringBuilder();
		}
		sb.append("Exception : "+ error.getMessage()+"\n");
		for (StackTraceElement ste : error.getStackTrace()) {
			sb.append("    " + ste.toString() + "\n");
		}
		if (error.getCause() != null) {
			sb.append("Caused by : \n");
			asLineString(sb, error.getCause());
		}
		return sb;		
	}
}
