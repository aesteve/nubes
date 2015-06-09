package io.vertx.nubes.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Singleton pattern implementation using enums.
 * 
 * See "Effective Java" by Joshua Bloch
 * 
 * @author aesteve
 */
public enum DateUtils {

    INSTANCE;

    private SimpleDateFormat iso8601Parser;

    private DateUtils() {
        // FIXME : use Java 8 API instead (with moments)
        iso8601Parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        iso8601Parser.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Date parseIso8601(String date) throws ParseException {
        return iso8601Parser.parse(date);
    }

    public String formatIso8601(Date date) {
        return iso8601Parser.format(date);
    }

}
