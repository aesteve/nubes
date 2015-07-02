# Vert.x Nubes

## Provides an annotation layer on top of vertx-web

/!\ Important note : It's a work in progress. Nubes hasn't been benchmarked yet. /!\

The main idea is to provide a different way to declare your routes than you'd do in a standard vertx-web project by providing a set of hopefully useful annotations / utilities on top of vertx-web.

The framework is designed to be fully extensible so that you can register and use your own annotations, types, marshallers, ... 

Keep in mind that at the end of the day, vertx-web's router is still there and fully accessible if you find yourself stuck in an edge case Nubes isn't designed to handle. This way, you should never, ever be stucked. You just have a set of additional utilities at your disposal.


## For the impatient, here's a basic example :

### A controller : 

```java
package com.peanuts.controllers;

@Controller("/peanuts")
public class PeanutsPages {
  
  public ENUM CharacterType {
    DOG, GIRL, BOY, BIRD;
  }
  
  @GET("/character")
  @View("character.hbs")
  public void getCharacter(RoutingContext context, @Param("type") CharacterType type) {
    switch(type) {
      case DOG: 
        context.put("name", "Snoopy");
        break;
      case BOY:
        context.put("name", "Charlie Brown");
        break;
      // ...
    }
    context.next()
  }
}
```

### The view : 

`web/views/character.hbs`

```html
<html>
  <body>
    Hello, I'm a Peanuts character, and my name is {{name}}
  </body>
</html>
```


`GET "/peanuts/character?type=DOG"` will return the following html view:

`Hello, I'm a Peanuts character, and my name is Snoopy`



### A JSON api example

```java
package com.peanuts.controllers;

@Controller("/api/1/peanuts")
@ContentType("application/json")
public class CharactersController {
  
  @GET("/character")
  public void getCharacter(RoutingContext context, @Param("type") CharacterType type, Payload<PeanutsCharacter> payload) {
    switch(type) {
      case DOG: 
        payload.set(new PeanutsCharacter(CharacterType.DOG, "Snoopy", snoopysBirthDate));
        break;
      // ...
    }
    context.next()
  }
  
  @POST("/character")
  public void createCharacter(RoutingContext context, @RequestBody PeanutsCharacter character) {
    yourDatabaseService.save(character, handler -> {
      context.next();  
    }); // save it using JDBC service, mongo service, hibernate service, etc.
  }
}
```

With this an example of domain object :

```java
package com.peanuts.model;

public class PeanutsCharacter {

  public ENUM CharacterType {
    DOG, GIRL, BOY, BIRD;
  }

  private CharacterType type;
  private String name;
  private Date birthDate;
  
  // getters, setters, constructors and stuff
}
```


`GET "/api/1/peanuts/characters?type=DOG"` with an `Accept` header containing `application/json` will return : 

```json
{
  "name":"Snoopy",
  "type":"DOG",
  "birthDate":"1950-11-04T08:00:00.000Z"
}
```


`POST "/api/1/peanuts/characters"` with the following request body : 

```json
{
  "name":"Snoopy",
  "type":"DOG",
  "birthDate":"1950-11-04T08:00:00.000Z"
}
```

Will save our favorite cartoon dog into the database, then return an HTTP 204.



## How it works

### VertxNubes as the entry point

The entry point of every work with the framwork is creatning a `VertxNubes` instance.

You'll notice the constructor takes two arguments : 

* a Vertx instance
* a JsonObject containing the configuration

Please take a look at [the configuration documentation](docs/CONFIG.md) for the available, mandatory or not, options.

Once you've created the VertxNubes instance, you need to `bootstrap` it. What it's gonna do is scanning your application classes (annotated with `@Controller`) in order to create the approriate Web routes/handlers and attach it to a vertx-web `Router`.

You can provide your own `Router`, if you want to keep to add custom routes and stuff in the standard vertx way. 

You can also let `VertxNubes` instanciate a `Router`. It's gonna return it to you once it's done bootstrapping.

```java
VertxNubes nubes = new VertxNubes(vertx, config);
nubes.bootstrap(res -> {
  if (res.succeeded()) {
    Router yourRouter = res.result();
    System.out.println("Everything's ready");
  } else {
    System.err.println("Something went wrong");
    res.cause().printStackTrace();
  }
});

```

You'll find a ton of examples in the tests of the project.

If you take a look at [the mock controllers](src/test/java/mock/controllers), you'll pretty much find everything that's possible to do with Nubes out of the box.

## The Controller layer

### What is a `@Controller` ?

A controller is a Java singleton defining a set of methods which will be translated into vertx-web handlers. (~= express middlewares).

It's important that your controller defines a no-argument constructor, VertxNubes expect that.

In a controller you'll find routes, annotated with `@GET`, `@POST`, `@PUT`, but also filters of two differents types : `@BeforeFilter` and `@AfterFilter`.

For each route in your controller, before filters will be executed before your actual route method, and after filters, well... after.


### Annotations

Nubes provides some default annotations. [Here's the list]().

But you can also define your own annotations, and attach vertx-web handlers to it.

In this case, you can register what Nubes calls "Annotation processors" which will be called before, after (or both) your method is invoked.

Nubes itself registers its own annotations using this API. For example, the `@ContentType({"application/json", "application/xml"})` annotation is bound to a `ContentTypeProcessor` which will : 

- check the `Accept` header of the request, and if it doesn't matches the MIME type you're handling, return a 406 status to the client
- find the most suitable MIME among the one the client specified in its `Accept` header and the one you specified in the body of the `ContentType` annotation
- inject this ContentType as a variable in the RoutingContext so that you can benefit from it
- position the `Content-Type` response header so that you don't have to care about it


[Read the annotations document](docs/ANNOTATIONS.md)

### Parameters

Parameters are automatically injected into every method at runtime, depending on the context of the request (parameters, body, ...).

For a complete list of available parameters (by default), see [the parameters documentation](docs/PARAMETERS.md).

But you can also register your own parameter resolvers by telling nubes : "When you find this type of parameter, resolve it like this".

Parameters can be resolved simply by their types (that's how Nubes injects the `RoutingContext` or the `EventBus` parameters if your method asks for it) or by a custom annotation you define.

[Read the parameters injection documentation](docs/PARAMETERS.MD)


## The View Layer

TODO : explain that template engines are created by the user, and bound to a file extension. Then how views are resolved, either by `@View("viewName.extension")` or through the `ViewResolver` parameter.

## The Service Layer

Services in Nubes are simple POJOs you register on the Nubes instance using `nubes.registerService(String name, Object serviceInstance)`. This way, you'll be able to access them from your controllers using the `@Service("someName")` annotation. 

### Standard services

Any POJO can be a service. Simply register it against Nubes.

### Async services

In some case, services take time to startup or to be closed. If you want your service to be started when Nubes bootstraps, just implements Nubes `Service` interface and register it, the same way you would register a POJO.

### RPC and service proxies

TODO : add `@ServiceProxy("address")` annotation. And explain how to use vertx's service proxy.

## SockJS

Nubes provides a simple way to declare SockJS handlers with annotations instead of handlers defined in vertx-web.


For example, to handle incoming sockets you would do the following.


```java
@SockJS("/sockjs/*")
public class SockJSController {
  @OnMessage
  public void onMessage(SockJSSocket emitter, Buffer message) {
    emitter.write(Buffer.buffer(message)); // simply echo the message back
  }
}
```

You can also use vertx-web's event-bus bridge if you want your users to access some addresses over the event-bus from client-side using SockJS.

@EventBusBridge("/sockjs/*")
@InboundPermitted("some-allowed-address")
public class BridgeController {

  @SOCKET_CREATED
  public void whenSocketIsCreated(BridgeEvent event) {
    // do what you want...
  }
  
  @REGISTER
  public void whenSomeUserRegisters(BridgeEvent event) {
    // do what you want...
  }
  
}
```

