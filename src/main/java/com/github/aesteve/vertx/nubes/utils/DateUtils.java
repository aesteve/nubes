package com.github.aesteve.vertx.nubes.utils;

import io.vertx.core.VertxException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Singleton pattern implementation using enums.
 * <p>
 * See "Effective Java" by Joshua Bloch
 *
 * @author aesteve
 */
public enum DateUtils {

  INSTANCE;

  private DatatypeFactory factory;

  DateUtils() {
    try {
      factory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException dtce) {
      throw new VertxException(dtce);
    }
  }

  public Date parseIso8601(String date) {
    XMLGregorianCalendar cal = factory.newXMLGregorianCalendar(date);
    return cal.toGregorianCalendar().getTime();
  }

  public String formatIso8601(Date date, TimeZone zone) {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(date);
    if (zone != null) {
      cal.setTimeZone(zone);
    }
    XMLGregorianCalendar calXml = factory.newXMLGregorianCalendar(cal);
    if (zone == null) {
      // display as UTC
      calXml = calXml.normalize();
    }
    return calXml.toXMLFormat();
  }

  public String formatIso8601(Date date) {
    return formatIso8601(date, null);
  }

}
