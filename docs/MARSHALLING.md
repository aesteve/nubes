## What is marshalling

In every REST API you have to send data to a client, or read data the client has sent to the server. Data is sent/received using a specific content-type.

Often, in your web application, you handle Java objects (POJOs). And these are the objects you want to read or write from/to the client.

Marshalling is the bridge between these two worlds : 

* the server-world using POJOs, 
* the client-world using XML, JSON, YAML, ...

If you've ever heard of libraries such as JAXB, Jackson, etc. you must be familiar with this concept.


## Where is marshalling used in Vertx-Nubes ?

Simple answer : 
* for reading the http request body (see `@RequestBody` annotation)
* for writing the http response body (see `Payload<T>` typed parameter)

## Default marshallers

By default, the framework comes with the Boon library to handle Json. If you're only using Json APIs and just want it to work, you should be satisfied. If you know about Boon, the serializer used by default is the annotation serializer. Which means that any of your objects you send has payload or read from the request body will be (un)marshalled with respect to the annotated fields (`JsonIgnore`, ...).

## How does it work ?

Basically, as an end-user, you'll register a marshaller for a certain content-type.

As you already know, you can define for each controller/route a set of accepted content-types. When the user makes a request, the framework will look for the best matching content-type among the ones you accepted and the ones the client knows how to handle.

Once this is done, if you set a response payload or want to read from the request body, the framework will look for the marshaller registered against this specific content-type, and call it.


In practice, let's take a look at the default json marshaller, registered for `application/json` content-type.

```java
public class BoonPayloadMarshaller implements PayloadMarshaller {

    protected JsonSerializer serializer;
    protected ObjectMapper mapper;

	
	public BoonPayloadMarshaller() {
		this.serializer = new JsonSerializerFactory().useAnnotations().create();
		this.mapper = JsonFactory.create();
	}
	
	@Override
	public<T> T unmarshallPayload(String body, Class<T> clazz) {
		return mapper.fromJson(body, clazz);
	}

	@Override
	public String marshallPayload(Object payload) {
		return serializer.serialize(payload).toString();
	}

	@Override
	public String marshallUnexpectedError(Throwable error, boolean displayDetails) {
		JsonObject json = new JsonObject();
		json.put("code", 500);
		if (displayDetails) {
			json.put("message", StackTracePrinter.asLineString(null, error));
		} else {
			json.put("message", "Internal Server Error");
		}
		return json.toString();
	}

	@Override
	public String marshallHttpError(HttpException error, boolean displayDetails) {
		JsonObject json = new JsonObject();
		json.put("code", error.getStatusCode());
		json.put("message", error.getStatusMessage());
		return json.toString();
	}

}

```

There are 4 methods to override in `PayloadMarshaller`.

* `marshallPayload` : from a POJO, you return a Json string
* `unmarshallPayload` : from a String (the request body as a String), you return a typed object
* `marshallHttpError` : from an http error (i.e. an error related to a well known http code : 404, 406, 400, ...) : how would you just tell the client about it
* `marshallUnexpectedError` : in case of a bug, (error 500), how would you tell the API client ? 

