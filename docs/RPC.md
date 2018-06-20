## Calling remote services through the event-bus : RPC

### What is RPC ?

RPC stands for Remote Procedure Call. It allows you to call a distant service as if it was just a local primitive on a local object.

Vert.x provides an useful project to achieve that, through the event-bus. The project is called [vertx service proxy](https://github.com/vert-x3/vertx-service-proxy). Please take a look at this project first to understand how to make some of your services available through the event-bus.

We're gonna create a simple service called `ParrotService` which is simply going to echo back some String passed as parameter.


```java
package foo.bar;

// imports

@ProxyGen
public interface ParrotService {
   public void echo(String msg, Handler<AsyncResult<String>> handler);
}
```

And its implementation :

```java
package foo.bar.impl;

public class ParrotServiceImpl {
   public void echo(String msg, Handler<AsyncResult<String>> handler) {
      handler.handle(Future.succeededFuture(msg));
   }
}
```

Thanks to service-proxy and Java's annotation processor tool, (see the `build.gradle` file to understand how to invoke the APT to have it generate the files correctly), the following classes will be generated : 

* foo.bar.ParrotServiceEBProxy
* foo.bar.ParrotServiceVertxProxyHandler


Then in a standard Vert.x you would have to deal with it (especially the proxy handler) and have it listen to some address over the event bus. Here comes Nubes to the rescue.


### @Proxify and @ServiceProxy

With Nubes, you'll be able to do two more things kinda "magically".


First, "mount" your service on a specific event bus address.

This is done on the implementation, not the interface. Here `ParrotServiceImpl` :

```java
package foo.bar.impl;

@Proxify("service.parrot")
public class ParrotServiceImpl {
   public void echo(String msg, Handler<AsyncResult<String>> handler) {
      handler.handle(Future.succeededFuture(msg));
   }
}
```

if you call Nubes method : `registerServiceProxy(new ParrotServiceImpl())` then Nubes will do all the stuff needed to make your service accessible from the event-bus at the address `service.parrot` (the one you specified thanks to `@ProxifyService` annotation.

*Note : In a future version, Nubes should be able to discover services on its own, without the need for you to call `registerService` or `registerServiceProxy`.*

Your ParrotService is now available through the event-bus ! You should be able to send messages at the `service.parrot` address and see what happens, but as you've discovered above, vertx-service-proxy generates more than that. And Nubes takes full profit of that.


Let's imagine, on some of your Nubes project, you want to invoke `ParrotService` as if it was local but this service as been deployer on a another Nubes instance (or simply a Verticle) across the network. Well, it should be as simple as that : 


```java
@Controller("/parrot")
public class ParrotController {

   @ServiceProxy("service.parrot")
   private ParrotService parrot;
   
   @POST
   public void annoyingNoisyParrot(@RequestBody String sent, HttpServerResponse response) {
      parrot.echo(sent, reply -> { // reply is an AsyncResult<String> remember
         response.end(reply.result());
      });
   }
}
``` 

Here you go, you're now able to do some fancy RPC with nothing more than a bunch of annotations, and let both Vert.x and Nubes do all the plumbing stuff for you.