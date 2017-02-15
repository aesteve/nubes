package integration.api.xml;

import integration.VertxNubesTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

// TODO : test errors

public class XmlApiTest extends VertxNubesTestBase {

	private final static String dogXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><dog><breed>Beagle</breed><name>Snoopy</name><puppy>true</puppy></dog>";

	@Test
	public void noContentType(TestContext context) {
		Async async = context.async();
		client().getNow("/xml/dog", response -> {
			context.assertEquals(406, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void wrongContentType(TestContext context) {
		Async async = context.async();
		client().get("/xml/dog", response -> {
			context.assertEquals(406, response.statusCode());
			response.bodyHandler(buff -> {
				context.assertEquals("Not acceptable", buff.toString("UTF-8"));
				async.complete();
			});
		}).putHeader(ACCEPT, "yourmum").end();
	}

	@Test
	public void getDomainObject(TestContext context) {
		Async async = context.async();
		getXML("/xml/dog", response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/xml", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				context.assertEquals(dogXML, buffer.toString("UTF-8"));
				async.complete();
			});
		});
	}

	@Test
	public void postSomeStuff(TestContext context) {
		Async async = context.async();
		sendXML("/xml/postdog", dogXML, response -> {
			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/xml", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				context.assertEquals(dogXML, buffer.toString("UTF-8"));
				async.complete();
			});
		});
	}
	
	@Test
	public void marshallException(TestContext context) {
		Async async = context.async();
		getXML("/xml/exception", response -> {
			context.assertEquals(500, response.statusCode());
			context.assertEquals("application/xml", response.getHeader(CONTENT_TYPE));
			response.bodyHandler(buffer -> {
				String xml = buffer.toString("UTF-8");
				context.assertNotNull(xml);
				try {
					Document doc = loadXMLFromString(xml);
					NodeList elements = doc.getElementsByTagName("message");
					Node node = elements.item(0);
					String msg = node.getTextContent();
					context.assertTrue(msg.indexOf("Exception : Manually thrown exception") > -1);
					async.complete();
				} catch(Exception e) {
					context.fail(e);
				}
			});
		});
	}
	
	private static Document loadXMLFromString(String xml) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return builder.parse(is);
	}

}
