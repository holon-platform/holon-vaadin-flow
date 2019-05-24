# Holon platform Vaadin Flow module

> Latest release: [5.2.10](#obtain-the-artifacts)

This is the __Vaadin Flow UI__ module of the [Holon Platform](https://holon-platform.com), which represents the platform support for the [Vaadin Flow](https://vaadin.com/flow) web applications platform, focusing on the user interface components, navigation and data binding features.

* A Java API to build (using _fluent_ builders), manage and use the web application UI components.
* Integration with the platform foundation architecture, such as the `Property` model and the `Datastore` API, the authentication, authorization and localization support.
* Routing targets query parameters injection using the `@QueryParameter` annotation, with extensible Java type conversion support.
* A `Navigator` API to manage the web application routing, with query parameters support.
* __Spring__ and __Spring Boot__ integration.

See the module [documentation](https://docs.holon-platform.com/current/reference/holon-vaadin-flow.html) for details.

Just like any other platform module, this artifact is part of the [Holon Platform](https://holon-platform.com) ecosystem, but can be also used as a _stand-alone_ library.

See [Getting started](#getting-started) and the [platform documentation](https://docs.holon-platform.com/current/reference) for further details.

## Previous Vaadin versions

The Holon platform Vaadin module is also available for previous Vaadin platform versions:

* [Vaadin 8 series](https://github.com/holon-platform/holon-vaadin)
* [Vaadin 7 series](https://github.com/holon-platform/holon-vaadin7)

## At-a-glance overview

_Component builders:_
```java
Div label = Components.label()
  .text("Label text")
  .width("200px")
  .build();
```

_Dialog:_
```java
Components.dialog.question(confirm -> {
    // handle user response (true/false)
  }).text("Default text", "message.code")
  .open();
```

_Item listing:_
```java
PropertyListing listing = PropertyListing.builder(SUBJECT) 
  .dataSource(datastore, TARGET)
  .withQueryFilter(NAME.isNotNull())
  .withQuerySort(ID.asc())
  .frozenColumns(1) 
  .withComponentColumn(item -> new Button(item.getValue(NAME)))
    .sortUsing(NAME)
    .displayAsFirst()
    .add()
  .multiSelect()
  .withSelectionListener(event -> {
    // ...
  })
  .build();
  
listing.refresh();
```

_Input components and forms:_
```java
Input<String> input = Input.string()
  .label("My label", "label.message.code")
  .maxLength(50)
  .build();
  
SingleSelect<String> singleSelect = Input.singleSelect(ID)
  .dataSource(datastore, TARGET, SUBJECT)
  .build();
  
PropertyInputForm form = PropertyInputForm.formLayout(SUBJECT)
  .hidden(ID)
  .propertyCaption(NAME, "My name", "name.message.code")
  .build();
  
form.validate();

PropertyBox value = form.getValue();
```

_Query parameters injection:_
```java
@Route("some/path")
public class View extends Div {

  @QueryParameter
  private String parameter1;
  
  @QueryParameter("myparam2")
  private Integer parameter2;
  
  @QueryParameter(value = "myparam3", required = true)
  private LocalDate parameter3;

}
```

_Navigator API:_
```java
Navigator navigator = Navigator.get();

navigator.navigateTo("some/path");

navigator.navigateBack();

navigator.navigation(View.class)
  .withQueryParameter("myparam", new Integer(1))
  .withQueryParameter("multi", "a", "b", "c")
  .navigate();
```

## Code structure

See [Holon Platform code structure and conventions](https://github.com/holon-platform/platform/blob/master/CODING.md) to learn about the _"real Java API"_ philosophy with which the project codebase is developed and organized.

## Getting started

### System requirements

The Holon Platform is built using __Java 8__, so you need a JRE/JDK version 8 or above to use the platform artifacts.

### Releases

See [releases](https://github.com/holon-platform/holon-vaadin-flow/releases) for the available releases. Each release tag provides a link to the closed issues.

### Obtain the artifacts

The [Holon Platform](https://holon-platform.com) is open source and licensed under the [Apache 2.0 license](LICENSE.md). All the artifacts (including binaries, sources and javadocs) are available from the [Maven Central](https://mvnrepository.com/repos/central) repository.

The Maven __group id__ for this module is `com.holon-platform.vaadin` and a _BOM (Bill of Materials)_ is provided to obtain the module artifacts:

_Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform.vaadin</groupId>
        <artifactId>holon-vaadin-flow-bom</artifactId>
        <version>5.2.10</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Using the Platform BOM

The [Holon Platform](https://holon-platform.com) provides an overall Maven _BOM (Bill of Materials)_ to easily obtain all the available platform artifacts:

_Platform Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform</groupId>
        <artifactId>bom</artifactId>
        <version>${platform-version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Build from sources

You can build the sources using Maven (version 3.3.x or above is recommended) like this: 

`mvn clean install`

## Getting help

* Check the [platform documentation](https://docs.holon-platform.com/current/reference) or the specific [module documentation](https://docs.holon-platform.com/current/reference/holon-vaadin-flow.html).

* Ask a question on [Stack Overflow](http://stackoverflow.com). We monitor the [`holon-platform`](http://stackoverflow.com/tags/holon-platform) tag.

* Report an [issue](https://github.com/holon-platform/holon-vaadin-flow/issues).

* A [commercial support](https://holon-platform.com/services) is available too.

## Examples

See the [Holon Platform examples](https://github.com/holon-platform/holon-examples) repository for a set of example projects.

## Contribute

See [Contributing to the Holon Platform](https://github.com/holon-platform/platform/blob/master/CONTRIBUTING.md).

[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/holon-platform/contribute?utm_source=share-link&utm_medium=link&utm_campaign=share-link) 
Join the __contribute__ Gitter room for any question and to contact us.

## License

All the [Holon Platform](https://holon-platform.com) modules are _Open Source_ software released under the [Apache 2.0 license](LICENSE).

## Artifacts list

Maven _group id_: `com.holon-platform.vaadin`

Artifact id | Description
----------- | -----------
`holon-vaadin-flow` | Core artifact
`holon-vaadin-flow-navigator` | The `Navigator` API
`holon-vaadin-flow-spring` | __Spring__ integration 
`holon-vaadin-flow-spring-boot` | __Spring Boot__ integration 
`holon-starter-vaadin-flow` | __Spring Boot__ _starter_ 
`holon-starter-vaadin-flow-undertow` | __Spring Boot__ _starter_ using Undertow as embedded servlet container
`holon-vaadin-flow-bom` | Bill Of Materials
`documentation-vaadin-flow` | Documentation
