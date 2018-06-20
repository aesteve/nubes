## Services and dependency injection

### What is a service in Nubes ?

A service can be a lot of things, but basically, it's a Java singleton (per Nubes instance) you'll be using from your controllers or fixtures to actually **do something**.

Very often, in web applications, you need to access storage, whether it's a database (SQL, ...) or an in-memory store (Redis, ...) or simply store some data on the file system.

A good example of service could be your Data Access Objects, dealing with low-level storage and returning Java objects to your controllers.


### Getting started : registering your services

To get started, a Service can be a simple java object. Let's imagine a Kennel where we're "storing" our dogs.


```java
public class Kennel {
   private List<Dog> dogs;
    
    
   public Kennel() {
      dogs = new ArrayList<>();
   } 
   
   public Dog adoptADog(int i) {
      return dogs.remove(i);
   }
   
   public void keepCareOf(Dog dog) {
      dogs.add(dog);
   }
   
   public Collection<Dog> inventory() {
      return dogs;
   }
   
   // ...
}
```

With a nubes instance created, you'll simply add the following : 

```java
nubes.registerService("kennel", new Kennel());
```

Now you're able to write, from your controller :

```java
@Controller("/api/dogs/")
@ContentType("application/json")
public class DogsController {

   @Service("kennel")
   private Kennel kennel;
   
   @GET
   public Collection<Dog> list() {
      return kennel.inventory();
   }
   
   @POST
   public Dog create(@RequestBody Dog dog) {
      kennel.keepCareOf(dog);
      return dog;
   }
}
```

The instance of Kennel will be automatically injected in every field annotated with `@Service("kennel")` in your controllers or fixtures.

### Example : dealing with Mongo

As you probably know, Vert.x provides an Mongo client to deal with a Mongo database.

An example of a service in Nubes could simply be the `MongoClient`.

```java
nubes.registerService("mongo", MongoClient.createShared(vertx));
```

Then you'll be able to access the mongo client from wherever you need to, simply using `@Service("mongo")`

### The Service interface

#### How to use it

Sometimes in your application, a simple Java object won't be enough to do everything you need to. And your services will either : 

* need to access the Vert.x instance (maybe a LocalMap, or SharedMap provided by Vert.x)
* start/stop asynchronously, and you need to wait for it to be started before doing anything
* do something on a periodic basis
* listen to the event-bus

To tackle these use cases, Nubes provides a Service interface you can implement in your own way. Let's have a look at the methods it defines :

```java
   public void init(Vertx vertx);

   public void start(Future<Void> future);

   public void stop(Future<Void> future);
```

`init` will be called when Nubes is bootstraping, in order to provide to the service the vert.x object it might (or might not, it's up to you) need internally to access local/shared maps, ...

`start` will be called then, and wait for the future passed as parameter to be completed before nubes' bootstrap method returns and is ready to go.

`stop` will be called when nubes is stopped, and will also wait for the future to be completed.

For example, our previous dog's Kennel could use a LocalMap named "kennel" instad of a simple ArrayList.

```java
// we're assuming Dog is marked as Shareable, and thus can be stored in Vert.x Shared Data
public class Kennel implements Service {

   private SharedData sd;

   @Override
   public void init(Vertx vertx) {
      sd = vertx.sharedData();
   }
   
   @Override
   public void start(Future<Void> future) {
     future.complete(); // here nothing is async, but we could imagine sending a message and waiting for an ACK
   }
   
   @Override
   public void start(Future<Void> future) {
     future.complete(); // here nothing is async, ...
   }
   
   public Collection<Dog> inventory() {
      return sd.getLocalMap("kennel").values();
   }
   
   
   public void keepCareOf(Dog dog) {
      sd.getLocalMap("kennel").put(dog.name, dog);
   }
   
   // ...
}
```

#### Helpers

But that's not all Services can do.

Service methods can also be annotated to indicate that they do something Vert.x applications often do :

* `@PeriodicTask` : indicates that this method should be invoked on a periodic basis (annotation parameter) using Vert.x `setPeriodic` feature
* `@Consumer` : indicates that this method should handle event bus messages sent to the address provided as annotation parameter

Let's take our dog's kennel once more and imagine it's tied to the event bus.

```java
public class Kennel implements Service {

   private SharedData sd;

   // [...] Stuff we've seen before
   
   @PeriodicTask(3000)
   public void logInventory() {
      sd.getLocalMap("kennel").values().forEach(dog -> {
         System.out.println("Dog : " + dog.name + " is in the kennel, and is fine, we're taking good care of him");
      });
   }
   
   @Consumer("dogs.report")
   public void reportDogStatus(Message incomingMsg) {
      Map<String, Dog> dogs = sd.getLocalMap("kennel");
      String dogsName = (String)incomingMsg.body();
      Dog dog = dogs.get(dogsName);
      if (dog != null) {
      	 incomingMsg.reply("Your dog " + dogsName + " seems to be fine, we're taking care of him");
      } else {
         incomingMsg.reply("Your dog " + dogsName + " seems to be missing... We'll chase him :|");
      }
   }
}
```

First, the service will log periodically the names of the dogs in the kennel into the console, nothing special here.
Then, the service will listen to the "dogs.report" address on the event bus and answer the message sender if the dog is in the kennel (and thus hopefully is in good shape...).