# Java Interface Template Library

## Table of contents
+ **[What is JITL](#what-is-jitl)**
+ **[Basic example (Jitl Core)](#basic-example-jitl-core)**
+ **[Advanced example (Jitl Core + Jtwig Template Engine + SQL Module)](#advanced-example-jitl-core-jtwig-template-engine-sql-module)**
+ **[File Extensions](#file-extensions)**
+ **[Template Engines](#template-engines)**
+ **[Modules](#modules)**
+ **[Interface Annotations](#tnterface-annotations)**
    + **[@BaseClasspath, @ClasspathTemplate and @InlineTemplate annotations](#baseclasspath-classpathtemplate-and-inlinetemplate-annotations)**
        + **[@BaseClasspath](#baseclasspath)**
        + **[@ClasspathTemplate](#classpathtemplate)**
        + **[@InlineTemplate](#inlinetemplate)**
    + **[@Param and @Params annotations](#param-and-params-annotations)**
    + **[@UseModule annotation](#usemodule-annotation)**
+ **[Maven](#maven)**
+ **[Related projects](#related-projects)**

## What is JITL?

JITL is a library that makes the job of rendering templates as simple as invoking a method. Using JITL, you only have to define a Java interface and JITL will find your template files in your *resources* folder. Method parameters are accessible within the template.

First, JITL renders the template using a [template engine](#template-engines). After that, it performs additional operations using a [module](#modules).

Take a look to de [basic example](#basic-example-jitl-core) and the [advanced example](#advanced-example-jitl-core-jtwig-template-engine-sql-module) in order to learn more.

## Basic example (Jitl Core)
```HtmlViews.java```:
```java
package com.example;

public interface HtmlViews {
    String login();
    String hello(@Param("username") String username);
}
```
```hello.tpl```:
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
```Main.java```:
```java
public class Main {
    public static void main(String[] args) {
        Jitl jitl = Jitl.builder().build();
        HtmlViews views = jitl.getInstance(HtmlViews.class);
        String renderedHtml = views.hello("world");
    }
}
```
In this example, ```com.example.HtmlViews.login()``` method will render the resource ```/com/example/html_views/login.tpl```, and ```com.example.HtmlViews.hello(String)``` method will render the resource ```/com/example/html_views/hello.tpl```.
+ By default, resources can have **.tpl** or **.txt** extensions. Additional file extensions can be specified when building the ```Jitl``` instance. See [File Extensions](#file-extensions) section.
+ Custom resource paths can be specified using annotations. See [@BaseClasspath, @ClasspathTemplate and @InlineTemplate annotations](#baseclasspath-classpathtemplate-and-inlinetemplate-annotations) section.

When ```views.hello("world")``` is called, the value of ```username``` parameter is passed to the template, and all the aparitions of ```$username``` are replaced by ```world```. The returned ```String``` is the result of the rendering process.

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

The rendering behaviour can be changed using different *template engines* and *modules*. See [Template Engines](#template-engines) and [Modules](#modules) sections.

## Advanced example (Jitl Core + Jtwig Template Engine + SQL Module)
```UsersRepository.java```:
```java
package com.example;

// imports...

@UseModule(SQLModule.class)
@BaseClasspath("/com/example/queries/users_")
public interface UsersRepository {
    List<User> findAllByType(String type, @Param("only_active") boolean onlyActiveUsers);
}
```
```find_all_by_type.sql```:
```sql
SELECT * FROM users WHERE type = :type {% if(only_active) %} AND active = 1 {% endif %};
```
```Main.java```:
```java
public class Main {
    public static void main(String[] args) {

        Jitl jitl = Jitl.builder()
            .setTemplateEngine(new JtwigTemplateEngine())
            .addModule(SQLModule.builder().build())
            .build();

        UsersRepository repository = jitl.getInstance(UsersRepository.class);
        List<User> users = repository.findAllByType("premium", true);

    }
}
```
In this example, ```com.example.UsersRepository.findAllByType(String, boolean)``` method will render the resource ```/com/example/queries/users_find_all_by_type.sql```.
+ See [File Extensions](#file-extensions) section.
+ See [@BaseClasspath, @ClasspathTemplate and @InlineTemplate annotations](#baseclasspath-classpathtemplate-and-inlinetemplate-annotations) section.

When ```repository.findAllByType("premium", true)``` is called, the value of ```type``` and ```onlyActiveUsers``` parameters are passed to the template. After executing ```JtwigTemplateEngine```, the result will be:

```sql
SELECT * FROM users WHERE type = :type AND active = 1;
```

Now, JITL is going to execute the ```SQLModule```. This module will execute the ```SELECT``` query on a database engine, and will transform the ```ResultSet``` into a ```List<User>```. The result will a Java list similar to:

```json
[
    { username = "harry.potter", type = "premium", active = true },
    { username = "tom.riddle", type = "premium", active = true },
]
```

Learn more about [Jtwig Template Engine](https://github.com/nestorrente/jitl-jtwig-template-engine) and [SQL Module](https://github.com/nestorrente/jitl-sql-module).

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

Additionally, modules can define its own extensions (i.e., ```jitl-sql-module``` defines the **.sql** extension). Extensions defined by a module have the highest priority, but they are only used when invoking a method whose interface was associated with that module. See [Modules](#modules) and [@UseModule annotation](#usemodule-annotation).

## Template Engines

A ```TemplateEngine``` is the object responsible of the rendering process. Only one ```TemplateEngine``` can be used in a single ```Jitl``` instance.

By default, JITL uses a simple template engine that replaces variables like ```$param``` with the value of the ```param``` parameter of the method. ```jitl-core``` project also provides a *no-operation* template engine that doesn't make any replacement, returning the original template contents.

JITL template engine can be changed invoking ```setTemplateEngine(TemplateEngine)``` from the ```JitlBuilder``` class:
```java
Jitl jitl = Jitl.builder()
    .setTemplateEngine(NoOpTemplateEngine.getInstance())
    .build();
```

Other projects, like [```jitl-jtwig-template-engine```](https://github.com/nestorrente/jitl-jtwig-template-engine) provide thirty-party powerful template engines.

## Modules

```Module```s are objects that can define additional file extensions and can perform additional operations **after** the rendering process (i.e. transform the result into another Java type). Many ```Module```s can be used in a single ```Jitl``` instance, but only one module can be used in each interface.

By default, JITL doesn't use any module. This means that no additional operations are performed after the rendering process. Each interface can use the ```@UseModule``` annotation in order to specify which ```Module``` would be used when invoking its methods. See [@UseModule annotation](#usemodule-annotation).

Other projects, like [```jitl-sql-module```](https://github.com/nestorrente/jitl-sql-module) provide powerful modules, allowing to perform some background operations and transform the result to another Java object (i.e., execute a SQL query in a database and transform the ```ResultSet``` to a *POJO*).

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
    String hello(@Param("username") String username);
}
```
In example 1, ```login()``` method will rended the resource ```/views/login.tpl```.
```java
// Example 2
package com.example;

@BaseClasspath("/web/view_")
public interface HtmlViews {
    String login();
    String hello(@Param("username") String username);
}
```
In example 2, ```login()``` method will rended the resource ```/web/view_login.tpl```.

**Note:** the class base-path is always absolute. This means that ```@BaseClasspath("/views/")``` and ```@BaseClasspath("views/")``` are the same path.

#### @ClasspathTemplate

This annotation can be used to change the method's relative-path that will be used for resolving its method's path. Absolute paths can also be specified using a beginning slash ```/```.
```java
package com.example;

public interface HtmlViews {

    @ClasspathTemplate("login_form")
    String login();

    @ClasspathTemplate("/views/hello")
    String hello(@Param("username") String username);

}
```
In the above example, ```login()``` method will rended the resource ```/com/example/html_views/login_form.tpl``` and ```hello(String)``` method will rended the resource ```/views/hello.tpl```.

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

By default, Java doesn't store the parameters names in .class files. Java code can be compiled using ```javac``` command with ```-parameters``` argument. However, you can specify parameters names using ```@Param``` and ```@Params``` annotations.
```java
package com.example;

public interface HtmlViews {

    String pageHeader(@Param("username") String user, @Param("title") String title);

    @Params({"username", "msg"})
    String hello(String username, String additionalMessage);

}
```
When any of these annotations is present, original parameters names are ignored.

### @UseModule annotation

Interfaces must be annotated with @UseModule in order to specify which module must be used when invoking its methods. Only one module can be used at the same time. When this annotation is not present, JITL doesn't use any module.
```java
package com.example;

@UseModule(SQLModule.class)
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
		<version>2.0.0</version>
	</dependency>
</dependencies>
```

## Related projects
+ [```jitl-jtwig-template-engine```](https://github.com/nestorrente/jitl-jtwig-template-engine)
+ [```jitl-sql-module```](https://github.com/nestorrente/jitl-sql-module)
