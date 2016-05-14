package com.github.aesteve.vertx.nubes.marshallers.impl;

import com.github.aesteve.vertx.nubes.exceptions.MarshallingException;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.utils.StackTracePrinter;
import io.vertx.core.VertxException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import static com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller.Constants.ERROR_CODE_KEY;
import static com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller.Constants.ERROR_KEY;
import static com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller.Constants.ERROR_MESSAGE_KEY;

public class JAXBPayloadMarshaller implements PayloadMarshaller {

  protected Marshaller marshaller;
  protected Unmarshaller unmarshaller;

  public JAXBPayloadMarshaller(Set<Class<?>> classes) throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));
    marshaller = jc.createMarshaller();
    unmarshaller = jc.createUnmarshaller();
  }

  @Override
  public <T> T unmarshallPayload(String body, Class<T> clazz) {
    try {
      return unmarshaller.unmarshal(loadXMLFromString(body), clazz).getValue();
    } catch (MarshallingException | JAXBException e) {
      throw new VertxException(e);
    }
  }

  @Override
  public String marshallPayload(Object payload) {
    StringWriter writer = new StringWriter();
    try {
      marshaller.marshal(payload, writer);
    } catch (JAXBException je) {
      throw new VertxException(je);
    }
    return writer.toString();
  }

  public static Document loadXMLFromString(String xml) throws MarshallingException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new MarshallingException(e);
    }
    try (StringReader reader = new StringReader(xml)){
      return builder.parse(new InputSource(reader));
    } catch (SAXException | IOException e) {
      throw new MarshallingException(e);
    }
  }

  @Override
  public String marshallUnexpectedError(Throwable error, boolean displayDetails) {
    return marshallError(500, error, null);
  }

  @Override
  public String marshallHttpStatus(int statusCode, String errorMessage) {
    return marshallError(statusCode, null, errorMessage);
  }

  private static String marshallError(int status, Throwable error, String message) {
    String errorMsg = message;
    if (message == null && error != null) {
      errorMsg = StackTracePrinter.asLineString(new StringBuilder(), error).toString();
    }
    return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<" + ERROR_KEY + ">\n" +
        "\t<" + ERROR_CODE_KEY + ">" + status + "</" + ERROR_CODE_KEY + ">\n" +
        "\t<" + ERROR_MESSAGE_KEY + ">\n<![CDATA[" +
        errorMsg +
        "]]>\n\t</" + ERROR_MESSAGE_KEY + ">\n" +
        "</" + ERROR_KEY + ">";
  }
}
