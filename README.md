# vertx-nubes
## Provides an annotation layer (jersey-like) on top of vertx-apex


It's a work in progress. If you're interested on what the actual objectives are, and how they could be achieved, please take a look at [the specifications](https://github.com/aesteve/vertx-mvc-specifications)

Feel free to comment and/or submit ideas in the specifications project.



# For the impatient, here's how it works :

## ViewController

### The controller : 

```java
package com.peanuts.controllers;

@Controller("/peanuts")
public class PeanutsPages {
  
  public ENUM CharacterType {
    DOG, GIRL, BOY, BIRD;
  }
  
  @Path("/character")
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



## A JSON api example

```java
package com.peanuts.controllers;

@Controller("/api/1/peanuts")
@ContentType("application/json")
public class CharactersController {
  
  @Path("/character")
  public void getCharacter(RoutingContext context, @Param("type") CharacterType type, Payload<PeanutsCharacter> payload) {
    switch(type) {
      case DOG: 
        payload.set(new PeanutsCharacter(CharacterType.DOG, "Snoopy", snoopysBirthDate));
        break;
      // ...
    }
    context.next()
  }
  
  @Path("/character")
  @POST
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



# How it works

## VertxNubes the nervous system

The starting point of every work with the framwork is creatning a `VertxNubes` instance.

You'll notice the constructor takes two arguments : 

* a Vertx instance
* a JsonObject containing the configuration

Please take a look at [docs/CONFIG.md](the configuration doc) for the configuration options.

Once you've created the VertxNubes instance, you need to bootstrap it. What it's gonna do is scanning your application classes (annotated with `@Controller`) in order to create the approriate Apex routes/handlers and attach it to an Apex router.

You can provide your own `Router`, if you want to keep to add custom routes and stuff, you can also let `VertxNubes` instanciate a `Router`. It's gonna return it to you once it's done bootstrapping.

## What is a `@Controller` ?

A controller is a Java singleton defining a set of vmethods which will be translated into Apex handlers.
It's important that your controller defines a no-argument constructor, VertxNubes expect that.

In a controller you'll find routes, annotated with `@Path` but also filters of two differents types : `@BeforeFilter` and `@AfterFilter`.

For each route in your controller, before filters will be executed before your actual route method, and after filters, well... after.

Parameters are automatically injected into every method at runtime. For a complete list of available parameters (by default), see [docs/PARAMETERS.md](the parameters injection documentation).

Every route, filter, or even the controller itself can be annotated with several annotation, which defines either handlers or processors.

## The RoutingContext

In every of your method, you have access to the RouginContext. This is important, since you can do pretty much everything you need in your method. Especially, you can do async stuff, and in this case you'll need to act on the RoutingContext the same you would do with Apex by calling either `context.fail(...)` or `context.next()`.

Keep in mind that under the hood, every of your method is mapped as an Apex handler. So you need to write what you would write with Apex.

## Annotations

### What is an AnnotationHandler

It's a simple Apex Handler<RoutingContext>, mapped by an annotation. This means when VertxNubes discovers a method annotated with this annotation, it will attach the Handler **before** your routing method is called.

### What is an AnnotationProcessor

It's a double Apex handler, with two `handle(RoutingContext)` methods. One will be called before the actual route method is called, one after.

### Let's see it in action : 

`@ContentType("application/json")` is an annotation processor.

Before the route method is called, it will check that the client `Accept` header effectively accepts the `application/json` content type.

After the route mehtod is called, it will try to find a response payload (response body) and marshall it as Json (if it's a Java object). (by default it's using Boon library).

Another thing you can think of as an `AnnotationProcessor` would be ETag handling.

Before the request reaches the routing method, you'd check the ETag header of the request, if by reading it you know the client is up-to-date, you would return an http 304 code. Else, you would invoke the actual routing method.

After your routing method has been called, you would then calculate the Etag for the response body.

The `VertxNubes` class provides an API so that you can register your own annotation handlers / processors. It (itself) uses this API for every default annotation (ContentType, ...).


## Type injection

One other feature of `VertxNubes` is the ability to call every method you defined with the right parameter instances, determined at runtime.

There are two types of parameters you can use : 

* Typed parameters
* Annotated parameters
 
### Typed parameters

Basically, the instance is defined by its type and the current RoutingContext. The most basic example is the RoutingContext itself.

```java
@Path("/dogs/mydog")
public void myDog(RoutingContext context) {
  log.info("User asked for his dog !");
  context.next();
}
```

Another example would be the Vertx instance. If you know about Apex, you know there's a `vertx()` method in the `RoutingContext` object.

So basically, if you define your method this way : 


```java
@Path("/dogs/mydog")
public void myDog(RoutingContext context, Vertx vertx) {
  vertx.setTimer(3000, timerId -> {
    log.info("User asked for his dog ! Sorry, I'm a little late to notice...");
  })
  context.next();
}
```

The vertx instance is simply injected by executing : `context.vertx()`. Well, you could have guessed, or even better, you could have done it yourself.

What's more interesting though, is to provide **your own** parameter injector. The `VertxNubes` holds a registry and itself uses the registry for the default types it knows how to handle (RoutingContext, Vertx, Payload, ...).

Simple implement the `ParameterInjector` interface, to provide the way to inject an object given its type and the RoutingContext.

For instance, let's imagine you stored user sessionIds in a `LocalMap` (that's probably not the best way to do it, but it's an example). You can provide your own UserInjector this way :

```java
public class UserInjector implements ParameterInjector<User> {
  public User resolve(RoutingContext context, Class<? extends User> clazz) {
    return User.fromMap(context.sharedData().localMap().get(context.request().getParam("token")));
  }
}
```

Then, before bootstrapping, you would call :

```java
vertxNubes.registerTypeParamInjector(User.class, new UserInjector());
```

In your routing methods (or filters) you're now able to write : 

```java
@Path("dogs/mydogs")
public void myDog(RoutingContext context, User user) {
  log.info("User asked for his dog, now I know which dog it is. It's : " + user.getDog());
  context.next();
}
```

You can imagine pretty much everything you need.


### Annotated parameters

TODO : write doc, using `@RequestBody` as an example.


-----


TODO : explain payload, marshallers and views.
