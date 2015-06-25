package com.github.aesteve.vertx.nubes.marshallers.impl;

import io.vertx.ext.web.impl.Utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.github.aesteve.vertx.nubes.exceptions.MarshallingException;
import com.github.aesteve.vertx.nubes.marshallers.PayloadMarshaller;
import com.github.aesteve.vertx.nubes.utils.StackTracePrinter;

public class JAXBPayloadMarshaller implements PayloadMarshaller {

	protected Marshaller marshaller;
	protected Unmarshaller unmarshaller;

	public JAXBPayloadMarshaller(String contextPath) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(contextPath, Utils.getClassLoader());
		marshaller = jc.createMarshaller();
		unmarshaller = jc.createUnmarshaller();
	}

	@Override
	public <T> T unmarshallPayload(String body, Class<T> clazz) throws MarshallingException {
		try {
			return unmarshaller.unmarshal(loadXMLFromString(body), clazz).getValue();
		} catch (Exception e) {
			throw new MarshallingException(e);
		}
	}

	@Override
	public String marshallPayload(Object payload) throws MarshallingException {
		StringWriter writer = new StringWriter();
		try {
			marshaller.marshal(payload, writer);
		} catch (JAXBException je) {
			throw new MarshallingException(je);
		}
		return writer.toString();
	}

	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	@Override
	public String marshallUnexpectedError(Throwable error, boolean displayDetails) {
		return marshallError(500, error, null);
	}

	@Override
	public String marshallHttpStatus(int statusCode, String errorMessage) {
		return marshallError(statusCode, null, errorMessage);
	}

	private String marshallError(int status, Throwable error, String message) {
		if (message == null && error != null) {
			message = StackTracePrinter.asLineString(null, error).toString();
		}
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		sb.append("<error>\n");
		sb.append("\t<code>500</code>\n");
		sb.append("\t<message>\n<![CDATA[");
		sb.append(message);
		sb.append("]]>\n\t</message>\n");
		sb.append("</error>");
		return sb.toString();
	}
}
