# Framework default annotations


## `@Controller` 

Indicates that this class is a controller and contains routes.

* Retention : Class
* Attributes :
** value (nullable) : the base path for every route defined in this controller
  

## `@Path`

Indicates that this method is a route.

* Retention : Method
* Attributes :
** value (mandatory) : the path for this route

## `@GET`, `@POST`, `@PUT`, `@DELETE`, `@OPTIONS`

Indicates that this route is accessible by this http method.

* Retention : Method
* Attributes : none

## `@AfterFilter`

A method which will be executed before every route defined in this controller. (see it as an pre-interceptor, or pre-filter).

* Retention : Method
* Attributes : 
** value : an Integer : the execution order of this filter (the smallest the first in execution order)

## `@BeforeFilter`

A method which will be executed after every route defined in this controller. (see it as a post-interceptor, or post-filter).

* Retention : Method
* Attributes : 
** value : an Integer : the execution order of this filter (the smallest the first in execution order)

## `@View`

Indicates that this route is a view, and should delegate to a template handler.

* Retention : Method
* Attributes :
** value (mandatory) : the name of the view (suffixed by the template extension : `@View("users.hbs")`)

## `@ContentType`

This route (or every route in the controller) can read/write the following content-types.

If a request is made to one of this routes with a non-acceptable accept header or content-type header, an http 406 error code will be returned.
The accept header is parsed in order to determine (and inject in routingContext.data()) the best content-type for this request. 

* Retention : Method or Class
* Attributes : 
** value : a list of content-types you know how to handle. Example : `@ContentType({"application/json", "text/xml"})`


TODO (WIP).