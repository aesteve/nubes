## What is marshalling

In every REST API you have to send data to a client, or read data the client has sent to the server. Data is sent/received using a specific content-type.

Often, in your web application, you handle Java objects (POJOs). And these are the objects you want to read or write from/to the client.

Marshalling is the bridge between these two worlds : 

* the server-world using POJOs, 
* the client-world using XML, JSON, YAML, ...

If you've ever heard of libraries such as JAXB, Jackson, etc. you must be familiar with this concept.


## Where is marshalling used in Vertx-MVC ?

Simple answer : 
* for reading the http request body (see `@RequestBody` annotation)
* for writing the http response body (see `Payload<T>`)

## How does it work ?

