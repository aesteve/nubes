# vertx-mvc
## Attempt to create an MVC framework on top of Vert.x 3


It's a work in progress. If you're interested on what the actual objectives are, and how they could be achieved, please take a look at [the specifications](https://github.com/aesteve/vertx-mvc-specifications)

Feel free to comment and/or submit ideas in the specifications project.



# Examples :

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
public class CharactersController extends JsonApiController {
  
  @Path("/character")
  public void getCharacter(RoutingContext context, @Param("type") CharacterType type) {
    switch(type) {
      case DOG: 
        setPayload(context, new PeanutsCharacter(CharacterType.DOG, "Snoopy", snoopysBirthDate);
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
