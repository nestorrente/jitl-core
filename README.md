# Java Interface Template Library

## Disclaimer

_This library is currently on development. Please, don't use it in production environments._

_Stable version 4.0.0 comming soon._

## Table of contents
+ **[What is JITL](#what-is-jitl)**
+ **[Basic example (Jitl Core)](#basic-example-jitl-core)**
+ **[Advanced example (Jitl Core + Jtwig Template Engine + SQL Processor)](#advanced-example-jitl-core--jtwig-template-engine--sql-processor)**
+ **[File Extensions](#file-extensions)**
+ **[Encoding](#encoding)**
+ **[Template Engines](#template-engines)**
+ **[Processors](#processors)**
+ **[Interface Annotations](#tnterface-annotations)**
    + **[@BaseClasspath, @ClasspathTemplate and @InlineTemplate annotations](#baseclasspath-classpathtemplate-and-inlinetemplate-annotations)**
        + **[@BaseClasspath](#baseclasspath)**
        + **[@ClasspathTemplate](#classpathtemplate)**
        + **[@InlineTemplate](#inlinetemplate)**
    + **[@Param and @Params annotations](#param-and-params-annotations)**
    + **[@UseProcessor annotation](#useprocessor-annotation)**
+ **[Maven](#maven)**
+ **[Related projects](#related-projects)**

## What is JITL?

JITL is a library that makes the job of rendering templates as simple as invoking a method. Using JITL, you only have to define a Java interface and JITL will find your template files in your *resources* folder. Method parameters are accessible within the template.

First, JITL renders the template using a [template engine](#template-engines). After that, it performs additional operations using a [processor](#processors).

Take a look to de [basic example](#basic-example-jitl-core) and the [advanced example](#advanced-example-jitl-core--jtwig-template-engine--sql-processor) in order to learn more.

## Basic example (Jitl Core)
**HtmlViews.java:**
```java
package com.example;

public interface HtmlViews {
    String login();
    String welcome(@Param("username") String username);
}
```
**welcome.tpl:**
```html
<html>
    <head>
        <title>Sample Page</title>
    </head>
    <body>
        <p>Hello, $username!</p>
    </body>
</html>
```
**Main.java:**
```java
public class Main {
    public static void main(String[] args) {
        Jitl jitl = Jitl.defaultInstance();
        HtmlViews views = jitl.getInstance(HtmlViews.class);
        String renderedHtml = views.welcome("world");
    }
}
```
In this example, ```com.example.HtmlViews.login()``` method will render the resource ```/com/example/html_views/login.tpl```, and ```com.example.HtmlViews.welcome(String)``` method will render the resource ```/com/example/html_views/welcome.tpl```.
+ By default, resources can have **.tmpl**, **.tpl** or **.txt** extensions. Additional file extensions can be specified when building the ```Jitl``` instance. See [File Extensions](#file-extensions) section.
+ Custom resource paths can be specified using annotations. See [@BaseClasspath, @ClasspathTemplate and @InlineTemplate annotations](#baseclasspath-classpathtemplate-and-inlinetemplate-annotations) section.

When ```views.welcome("world")``` is called, the value of ```username``` parameter is passed to the template, and all the aparitions of ```$username``` are replaced by ```world```. The returned ```String``` is the result of the rendering process:
```html
<html>
    <head>
        <title>Sample Page</title>
    </head>
    <body>
        <p>Hello, world!</p>
    </body>
</html>
```
The rendering behaviour can be changed using different *template engines* and *processors*. See [Template Engines](#template-engines) and [Processors](#processors) sections.

## Advanced example (Jitl Core + Jtwig Template Engine + SQL Processor)
**NOTE:** This example uses [Jtwig Template Engine](https://github.com/nestorrente/jitl-jtwig-template-engine) and [SQL Processor](https://github.com/nestorrente/jitl-sql-processor).

**UsersRepository.java:**
```java
package com.example;

// imports...

@UseProcessor(SQLProcessor.class)
@BaseClasspath("/com/example/queries/users_")
public interface UsersRepository {
    List<User> findAllByType(String type, @Param("only_active") boolean onlyActiveUsers);
}
```
**users_find_all_by_type.sql:**
```sql
SELECT * FROM users WHERE type = :type {% if(only_active) %} AND active = 1 {% endif %};
```
**Main.java:**
```java
public class Main {
    public static void main(String[] args) {

        try(Connection connection = /* ... */) {

	        Jitl jitl = Jitl.builder()
	            .setTemplateEngine(new JtwigTemplateEngine())
	            .addProcessor(SQLProcessor.defaultInstance(connection))
	            .build();
	
	        UsersRepository repository = jitl.getInstance(UsersRepository.class);
	        List<User> users = repository.findAllByType("premium", true);

        }

    }
}
```
In this example, ```com.example.UsersRepository.findAllByType(String, boolean)``` method will render the resource ```/com/example/queries/users_find_all_by_type.sql```.
+ See [File Extensions](#file-extensions) section.
+ See [@BaseClasspath, @ClasspathTemplate and @InlineTemplate annotations](#baseclasspath-classpathtemplate-and-inlinetemplate-annotations) section.

When ```repository.findAllByType("premium", true)``` is called, the value of ```type``` and ```onlyActiveUsers``` parameters are passed into the template engine renamed as ```type``` and ```only_active``` (as specified by the ```@Param``` annotation). After executing ```JtwigTemplateEngine```, the result will be:

```sql
SELECT * FROM users WHERE type = :type AND active = 1;
```

Now, JITL is going to execute the ```SQLProcessor```. This processor will execute the ```SELECT``` query on a database engine, and will transform the ```ResultSet``` into a ```List<User>```.

Notice that processors can also access parameter values also. ```SQLProcessor``` will use a JDBC ```PreparedStatement``` in order to pass the ```:type``` parameter value into the query.

The result of the execution will be a Java ```List<User>``` corresponding to the following JSON structure:

```json
[
    { "username": "harry.potter", "type": "premium", "active": true },
    { "username": "tom.riddle", "type": "premium", "active": true }
]
```

Learn more about [Jtwig Template Engine](https://github.com/nestorrente/jitl-jtwig-template-engine) and [SQL Processor](https://github.com/nestorrente/jitl-sql-processor).

## File Extensions

By default, JITL tries to find a resource template (i.e. ```/views/login```) without adding any file extension. If the resource is not found, then **.tpl** and **.txt** extensions are used (trying to find ```/views/login.tpl``` or ```/views/login.txt``` resources).

When building a ```Jitl``` instance, additional file extensions can be defined. Let's see an example:

```java
Collection<String> extensions = Arrays.asList("js", "css");

Jitl jitl = Jitl.builder()
    .addFileExtension("php")
    .addFileExtensions("htm", "html")
    .addFileExtensions(extensions)
    .build();
```

Last added extensions have higher priority.

Additionally, processors can define its own extensions (i.e., [SQL Processor](https://github.com/nestorrente/jitl-sql-processor) defines the **.sql** extension). Extensions defined by a processor have the highest priority, but they are only used when invoking a method whose interface was associated with that processor. See [Processors](#processors) and [@UseProcessor annotation](#useprocessor-annotation).

## Encoding

In order to specify the character set of the template files, JITL interfaces can be annotated with ```@Encoding``` annotation. This annotation receives a ```String``` representing the name of a character set. Let's see an example:

**HtmlViews.java:**
```java
package com.example;

@Encoding("UTF-8")
public interface HtmlViews {
    String login();
    String welcome(@Param("username") String username);
}
```

Internally, JITL uses the method [```Charset.forName(String charsetName)```](https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html#forName-java.lang.String-) for getting the corresponding ```Charset``` instance. When ```@Encoding``` is not present, [```Charset.defaultCharset()```](https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html#defaultCharset--) is used.

## Template Engines

A ```TemplateEngine``` is the object responsible of the rendering process. Only one ```TemplateEngine``` can be used in a single ```Jitl``` instance.

By default, JITL uses a simple template engine that replaces variables like ```$param``` with the value of the ```param``` parameter of the method. ```jitl-core``` project also provides a *no-operation* template engine that doesn't make any replacement, returning the original template contents.

JITL template engine can be changed using the ```setTemplateEngine(TemplateEngine)``` method from the ```JitlBuilder``` class. For example, this is how we tell JITL to use the built-in *no-operation* template engine:
```java
Jitl jitl = Jitl.builder()
    .setTemplateEngine(NoOpTemplateEngine.getInstance())
    .build();
```

You can define your own ```TemplateEngine```s by implementing [```TemplateEngine```](src/main/java/com/nestorrente/jitl/template/TemplateEngine.java) interface.

Other projects, like [```jitl-jtwig-template-engine```](https://github.com/nestorrente/jitl-jtwig-template-engine) provide thirty-party powerful template engines.

## Processors

```Processor```s are objects that can define additional file extensions and can perform additional operations **after** the rendering process (i.e. transform the result into another Java type). Many ```Processor```s can be used in a single ```Jitl``` instance, but only one processor can be used in each interface.

By default, JITL doesn't use any processor. This means that no additional operations are performed after the rendering process. Each interface can use the ```@UseProcessor``` annotation in order to specify which ```Processor``` would be used when invoking its methods. See [@UseProcessor annotation](#useprocessor-annotation).

You can define your own ```Processor```s by extending [```com.nestorrente.jitl.processor.Processor```](src/main/java/com/nestorrente/jitl/processor/Processor.java) class.

Other projects, like [```jitl-sql-processor```](https://github.com/nestorrente/jitl-sql-processor) provide powerful processors, allowing to perform some background operations and transform the result to another Java object (i.e., execute a SQL query in a database and transform the ```ResultSet``` into a *POJO*).

## Interface Annotations
There are some annotations that can be used to change the default template path, the parameters names, etc.

### @BaseClasspath, @ClasspathTemplate and @InlineTemplate annotations

By default, JITL searches templates in the classpath using the class and method names, using *underscore_case* instead of  *CamelCase*.

For example, ```login()``` method from ```com.example.HtmlViews``` class will rended the resource ```/com/example/html_views/login.tpl```, where ```/com/example/html_views/``` is the class' base-path, and ```login.tpl``` is the method relative-path.

Let's see some annotations that will help us to change this behaviour.

#### @BaseClasspath

This annotation can be used to change the class' base-path that will be used for resolving its method's path.
```java
// Example 1
package com.example;

@BaseClasspath("/views/")
public interface HtmlViews {
    String login();
    String welcome(@Param("username") String username);
}
```
In example 1, ```login()``` method will rended the resource ```/views/login.tpl```.
```java
// Example 2
package com.example;

@BaseClasspath("/web/view_")
public interface HtmlViews {
    String login();
    String welcome(@Param("username") String username);
}
```
In example 2, ```login()``` method will rended the resource ```/web/view_login.tpl```.

**Note:** the class base-path is always absolute. This means that ```@BaseClasspath("/views/")``` and ```@BaseClasspath("views/")``` refer to the same path.

#### @ClasspathTemplate

This annotation can be used to change the method's relative-path that will be used for resolving its method's path. Absolute paths can also be specified using a beginning slash ```/```.
```java
package com.example;

public interface HtmlViews {

    @ClasspathTemplate("login_form")
    String login();

    @ClasspathTemplate("/views/welcome")
    String welcome(@Param("username") String username);

}
```
In the above example, ```login()``` method will rended the resource ```/com/example/html_views/login_form.tpl``` and ```welcome(String)``` method will rended the resource ```/views/welcome.tpl```.

Using this annotation without an specific value has no effect. The following examples are equivalent:
```java
package com.example;

public interface HtmlViews {
    String login();
}
```
```java
package com.example;

public interface HtmlViews {
    @ClasspathTemplate
    String login();
}
```

#### @InlineTemplate

This annotation can be used in order to specify an inline template instead of a resource file.
```java
package com.example;

public interface HtmlViews {

    @InlineTemplate("<h1>$title</h1>")
    String pageHeader(String title);

}
```

### @Param and @Params annotations

By default, Java doesn't store the parameters names in ```.class``` files. Java code can be compiled using ```javac``` command with ```-parameters``` argument in order to preserve parameter names in ```.class``` file. However, you can specify your own parameters names using ```@Param``` and ```@Params``` annotations.
```java
package com.example;

public interface HtmlViews {

    String pageHeader(@Param("username") String user, @Param("title") String title);

    @Params({"username", "msg"})
    String welcome(String username, String additionalMessage);

}
```
When any of these annotations is present, original parameters names are ignored.

### @UseProcessor annotation

Interfaces must be annotated with ```@UseProcessor``` in order to specify which processor must be used after the rendering process when invoking its methods. Only one processor can be used at the same time. When this annotation is not present, JITL doesn't use any processor.
```java
package com.example;

@UseProcessor(SQLProcessor.class)
public interface DataAccess {
    List<User> getUsers();
}
```

## Maven

```xml
<repositories>
	<repository>
		<id>jcenter</id>
		<url>https://jcenter.bintray.com/</url>
	</repository>
</repositories>

<dependencies>
	<dependency>
		<groupId>com.nestorrente</groupId>
		<artifactId>jitl-core</artifactId>
		<version>4.0.0</version>
	</dependency>
</dependencies>
```

## Related projects
+ [```jitl-jtwig-template-engine```](https://github.com/nestorrente/jitl-jtwig-template-engine)
+ [```jitl-sql-processor```](https://github.com/nestorrente/jitl-sql-processor)
