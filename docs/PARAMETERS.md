

Every public method defined in a controller will be evaluated to inject the correct parameter values.

There are two types of parameters.

* By type : they're not annotated, only the type tells the framwork how to resolve the object instance
* By annotation : the type isn't enough to determine the object instance, in this case the annotation is evaluated

## Default parameters (already provided by the framework)

* By type :

** `RoutingContext` simply the RoutingContext of the request, as specified in Vertx Apex
** `Vertx` the vertx instance which is handling the current request
** `PaginationContext` an utility object holding pagination parameters, see [the pagination documentation](PAGINATION.md).
** `Payload` an object which holds the response body you want to send (as a Java object). See the [marshalling data documentation](MARSHALLING.md).

* By annotation :

** `@CookieValue("my.cookie") Cookie cookie` the value of a given cookie (here : "my.cookie")
** `@Header("Accept") String acceptHeader` some http request header 
** `@Param("from") Date from` a parameter of the http request
** `@PathParam("userId") Long userId` a parameter captured in route path
** `@Params YourObject backedParameters` fulfills an object with the whole parameters' map (from request query params / path params) 
** `@RequestBody YourObject unmarshalledFromRequest` the unmarshalled request body as an object of your type

## Extending the framework

You can provide your own "injectors". An injector is an interface you have to implement in order to tell the framework how to inject a parameter instance into the controller's method.

The framework itself uses this API in order to manage the inject parameters mentionned above.

Simply call `VertxMVC.registerXXXInjector` where XXX is either

* Typed
or 
* Annotated

### Typed parameters

The type of the parameter (and the RoutingContext) is sufficient to determine the object instance. You don't need any more info.

In this case you should implement `io.vertx.mvc.injectors.typed.ParamInjector`.

To help you understand, let's have a look at a very simple existing example : the `VertxInjector` (that the framework uses).

```java
public class VertxParamInjector implements ParamInjector<Vertx> {

	@Override
	public Vertx resolve(RoutingContext context) {
		return context.vertx();
	}

}
```

Nothing really interesting to mention here :)

Now let's imagine you store a map of : 
```json
{
	"token":"userName"
}
```

somewhere (maybe in `vertx.sharedData().localMap()` ? even though it's probably not a bes practice... but let's imagine...). 

You could write an `UserParamInjector` like this :

```java
public class UserParamInjector implements ParamInjector<User> {

	@Override
	public User resolve(RoutingContext context) {
		Vertx vertx = context.vertx();
		String token = context.request().getParam("token");
		if (token == null) {
			return null;
		}
		return (User)vertx.sharedData().localMap().get(token);
	}

}
```

Another example : you could write your own `SearchCriteria` class, mapped on request query parameters.

```java
public class SearchCriteriaInjector implements ParamInjector<SearchCriteria> {

	@Override
	public SearchCriteria resolve(RoutingContext context) {
		HttpServerRequest request = context.request();
		SearchCriteria crit = new SearchCriteria();
		crit.setOrderBy(request.getParam("orderBy"));
		String rawFilters = request.getParam("filters");
		for (String filter : rawFilters.split(",")) {
			crit.addFilter(filter");
		}
		crit.setOrderClause(request.getParam("asc") != null ? OrderBy.ASC : OrderBy.DESC);
		return crit;
	}

}
```

Now in your controller method, you can just write : 

```java
@Controller("/users")
public class MyController {

	@Path("")
	public void getUsers(RoutingContext context, SearchCriteria crit) {
		List<User> users = yourSimpleDAO.getUsers(crit);
		System.out.println("I found : "+users.size()+" users !");
		context.next();
	}

}
```