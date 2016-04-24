package com.github.aesteve.vertx.nubes.utils;

public interface StackTracePrinter {

  static StringBuilder asHtml(StringBuilder sb, Throwable error) {
    sb.append("<div class=\"exception\">");
    sb.append("<div class=\"exception-msg\">").append(error.getMessage()).append("</div>");
    sb.append("<ul class=\"stacktrace\">");
    for (StackTraceElement ste : error.getStackTrace()) {
      sb.append("<li>").append(ste.toString()).append("</li>");
    }
    sb.append("</ul>");
    sb.append("</div>");
    if (error.getCause() != null) {
      sb.append("<div class=\"caused-by\">Caused by : </div>");
      asHtml(sb, error.getCause());
    }
    return sb;
  }

  static StringBuilder asLineString(StringBuilder sb, Throwable error) {
    sb.append("Exception : ").append(error.getMessage()).append("\n");
    for (StackTraceElement ste : error.getStackTrace()) {
      sb.append("    ").append(ste.toString()).append("\n");
    }
    if (error.getCause() != null) {
      sb.append("Caused by : \n");
      asLineString(sb, error.getCause());
    }
    return sb;
  }
}
