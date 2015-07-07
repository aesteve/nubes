## Framework supported Configuration

Here is the list of all supported configurations (for now):

### `host`

* **Type:**   *String*

* **Description:**   This is the hostname of your server.

* **Default:**   Default value (only for NubesServer instance) is "localhost".

### `port`

* **Type:**   *int*

* **Description:**   This is the port of your server.

* **Default:**   Default value (only for NubesServer instance) is 9000.

### `src-package`


* **Type:**   *String*

* **Description:**   This is the base package of your project, it should look like "user.company.projectname". This is probably the only key which should always be set, for it is used to set default values for the other configuration keys.

* **Default:**   You shouldn't need that, but default value is "src.package".


### `controller-packages`


* **Type:**   *JsonArray*

* **Description:**   This is a JsonArray, which should contain all the packages where your controllers are located. 

* **Default:**	 If the key is missing, the value will be a concatenation of the *src-package* value with ".controllers". You see why *src-package* definition is important now? :)


### `fixture-packages`


* **Type:**   *JsonArray*

* **Description:**   This is a JsonArray, which should contain all the packages where your fixtures are located. 

* **Default:**  *src-package* value concat with ".fixtures".


### `domain-package`


* **Type:**   *String*

* **Description:**   This will set the package of your domains.

* **Default:**	  *src-package* value concat with ".domains".


### `verticle-package`


* **Type:**   *String*

* **Description:**   This will set the package of your verticles.

* **Default:**	  *src-package* value concat with ".verticles".


### `services`


* **Type:**   *JSonObject*

* **Description:**  This is a JsonObject which contains all the services to be registered by Nubes. The key is the service name (String), and the value is the service class name (also a String).

* **Default:**   No default value.


### `templates`


* **Type:**   *JsonArray*

* **Description:**   A JsonArray (of String) which contains all templates supported by the application. The expected values are: "hbs" for Handlebars, "jade" for Jade, "templ" for MVEL and "thymeleaf" for Thymeleaf (for the last one, you need to use .html file extension).

* **Default:**   No default value.


### `throttling`


TODO : 

* count
* time-frame
* time-unit


### `webroot`


* **Type:**   *String*

* **Description:**   This will set the webroot for your application.

* **Default:**	  default value is "web/assets".


### `static-path`


* **Type:**   *String*

* **Description:**   This will set the path for your static files in your application.

* **Default:**	  default value is "/assets".
* 
### `views-dir`


* **Type:**   *String*

* **Description:**   This will set the path for your template files in your application.

* **Default:**	  default value is "web/views".


### `Configuration example`

Here is an example of a valid conf.json file :

```json
{
	"host":"localhost",
	"port":8080,
  	"src-package":"mycompany.myproject",
  	"domain-package":"mycompany.myproject.domains",
  	"fixture-packages":["mycompany.myproject.fixtures"],
  	"controller-packages":["mycompany.myproject.controller"],
  	"services":{"taskService":"mycompany.myproject.services.TaskService"},
  	"templates":["hbs"]

}
```

You can see that the *verticle-package* key is missing, the value in the configuration will then be "mycompany.myproject.verticles".
