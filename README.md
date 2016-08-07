# Cloud Foundry Java Client
The `cf-java-client` project is a Java language binding for interacting with a Cloud Foundry instance.  The project is broken up into a number of components which expose different levels of abstraction depending on need.

* `cloudfoundry-client` – Interfaces, request, and response objects mapping to the [Cloud Foundry REST APIs][a].  This project has no implementation and therefore cannot connect a Cloud Foundry instance on its own.
* `cloudfoundry-client-reactor` – The default implementation of the `cloudfoundry-client` project.  This implementation is based on the Reactor Netty [`HttpClient`][h].
* `cloudfoundry-operations` – An API and implementation that corresponds to the [Cloud Foundry CLI][c] operations.  This project builds on the `cloudfoundry-client` and therefore has a single implementation.
* `cloudfoundry-maven-plugin` / `cloudfoundry-gradle-plugin` – Build plugins for [Maven][m] and [Gradle][g].  These projects build on `cloudfoundry-operations` and therefore have single implementations.

Most projects will need two dependencies; the Operations API and an implementation of the Client API.  For Maven, the dependencies would be defined like this:

```xml
<dependencies>
    <dependency>
        <groupId>org.cloudfoundry</groupId>
        <artifactId>cloudfoundry-client-reactor</artifactId>
        <version>2.0.0.BUILD-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.cloudfoundry</groupId>
        <artifactId>cloudfoundry-operations</artifactId>
        <version>2.0.0.BUILD-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-core</artifactId>
        <version>3.0.0.BUILD-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>io.projectreactor.ipc</groupId>
        <artifactId>reactor-netty</artifactId>
        <version>0.5.0.BUILD-SNAPSHOT</version>
    </dependency>
    ...
</dependencies>
```

The artifacts can be found in the Spring release, milestone, and snapshot repositories:

```xml
<repositories>
    <repository>
        <id>spring-releases</id>
        <name>Spring Releases</name>
        <url>http://repo.spring.io/release</url>
    </repository>
    ...
</repositories>
```

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>http://repo.spring.io/milestone</url>
    </repository>
    ...
</repositories>
```

```xml
<repositories>
    <repository>
        <id>spring-snapshots</id>
        <name>Spring Snapshots</name>
        <url>http://repo.spring.io/snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    ...
</repositories>
```

For Gradle, the dependencies would be defined like this:

```groovy
dependencies {
    compile 'org.cloudfoundry:cloudfoundry-client-reactor:2.0.0.BUILD-SNAPSHOT'
    compile 'org.cloudfoundry:cloudfoundry-operations:2.0.0.BUILD-SNAPSHOT'
    compile 'io.projectreactor:reactor-core:3.0.0.BUILD-SNAPSHOT'
    compile 'io.projectreactor.ipc:reactor-netty:0.5.0.BUILD-SNAPSHOT'
    ...
}
```

The artifacts can be found in the Spring release, milestone, and snapshot repositories:

```groovy
repositories {
    maven { url 'http://repo.spring.io/release' }
    ...
}
```

```groovy
repositories {
    maven { url 'http://repo.spring.io/milestone' }
    ...
}
```

```groovy
repositories {
    maven { url 'http://repo.spring.io/snapshot' }
    ...
}
```

## Usage
Both the `cloudfoundry-operations` and `cloudfoundry-client` projects follow a ["Reactive"][r] design pattern and expose their responses with [Project Reactor][p] `Monos`s and `Flux`s.

### `CloudFoundryClient`, `DopplerClient`, `UaaClient` Builders

The lowest-level building blocks of the API are `ConnectionContext` and `TokenProvider`.  These types are intended to be shared between instances of the clients, and come with out of the box implementations.  To instantiate them, you configure them with builders:

```java
DefaultConnectionContext.builder()
    .apiHost(apiHost)
    .build();

PasswordGrantTokenProvider.builder()
    .password(password)
    .username(username)
    .build();
```

In Spring-based applications, you'll want to encapsulate them in bean definitions:

```java
@Bean
DefaultConnectionContext connectionContext(@Value("${cf.apiHost}") String apiHost) {
    return DefaultConnectionContext.builder()
        .apiHost(apiHost)
        .build();
}

@Bean
PasswordGrantTokenProvider tokenProvider(@Value("${cf.username}") String username,
                                         @Value("${cf.password}") String password) {
    return PasswordGrantTokenProvider.builder()
        .password(password)
        .username(username)
        .build();
}
```

`CloudFoundryClient`, `DopplerClient`, and `UaaClient` are only interfaces.  Each has a [Reactor][p]-based implementation.  To instantiate them, you configure them with builders:

```
ReactorCloudFoundryClient.builder()
    .connectionContext(connectionContext)
    .tokenProvider(tokenProvider)
    .build();

ReactorDopplerClient.builder()
    .connectionContext(connectionContext)
    .tokenProvider(tokenProvider)
    .build();

ReactorUaaClient.builder()
    .connectionContext(connectionContext)
    .tokenProvider(tokenProvider)
    .build();
```

In Spring-based applications, you'll want to encapsulate them in bean definitions:

```
@Bean
ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
    return ReactorCloudFoundryClient.builder()
        .connectionContext(connectionContext)
        .tokenProvider(tokenProvider)
        .build();
}

@Bean
ReactorDopplerClient dopplerClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
    return ReactorDopplerClient.builder()
        .connectionContext(connectionContext)
        .tokenProvider(tokenProvider)
        .build();
}

@Bean
ReactorUaaClient uaaClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
    return ReactorUaaClient.builder()
        .connectionContext(connectionContext)
        .tokenProvider(tokenProvider)
        .build();
}
```

### `CloudFoundryOperations` Builder

The `CloudFoundryClient`, `DopplerClient`, and `UaaClient`s provide direct access to the raw REST APIs.  This level of abstraction provides the most detailed and powerful access to the Cloud Foundry instance, but also requires users to perform quite a lot of orchestration on their own.  Most users will instead want to work at the `CloudFoundryOperations` layer.  Once again this is only an interface and the default implementation of this is the `DefaultCloudFoundryOperations`.  To instantiate one, you configure it with a builder:

**NOTE:** The `DefaultCloudfoundryOperations` type does not require all clients in order to run.  Since not all operations touch all kinds of clients, you can selectively configure the minimum needed.  If a client is missing, the first invocation of a method that requires that client will return an error.

```java
DefaultCloudFoundryOperations.builder()
    .cloudFoundryClient(cloudFoundryClient)
    .dopplerClient(dopplerClient)
    .uaaClient(uaaClient)
    .organization("example-organization")
    .space("example-space")
    .build();
```

In Spring-based applications, you'll want to encapsulate this in a bean definition as well:

```java
@Bean
DefaultCloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient,
                                                     DopplerClient dopplerClient,
                                                     UaaClient uaaClient,
                                                     @Value("${cf.organization}") String organization,
                                                     @Value("${cf.space}") String space) {
    return DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .dopplerClient(dopplerClient)
            .uaaClient(uaaClient)
            .organization(organization)
            .space(space)
            .build();
}
```

### `CloudFoundryOperations` APIs

Once you've got a reference to the `CloudFoundryOperations`, it's time to start making calls to the Cloud Foundry instance.  One of the simplest possible operations is list all of the organizations the user is a member of.  The following example does three things:

1. Requests a list of all organizations
1. Extracts the name of each organization
1. Prints the name of the each organization to `System.out`

```java
cloudFoundryOperations.organizations()
    .list()
    .map(Organization::getName)
    .subscribe(System.out::println);
```

To relate the example to the description above the following happens:

1. `.list()` – Lists the Cloud Foundry organizations as a `Flux` of elements of type `Organization`.
1. `.map(...)` – Maps each organization to its name (type `String`).  This example uses a method reference; the equivalent lambda would look like `organization -> organization.getName()`.
1. `subscribe...` – The terminal operation that receives each name in the `Flux`.  Again, this example uses a method reference and the equivalent lambda would look like `name -> System.out.println(name)`.

### `CloudFoundryClient` APIs

As mentioned earlier, the `cloudfoundry-operations` implementation builds upon the `cloudfoundry-client` API.  That implementation takes advantage of the same reactive style in the lower-level API.  The implementation of the `Organizations.list()` method (which was demonstrated above) looks like the following (roughly):

```java
cloudFoundryClient.organizations()
    .list(ListOrganizationsRequest.builder()
        .page(1)
        .build())
    .flatMap(response -> Flux.fromIterable(response.getResources))
    .map(resource -> Organization.builder()
        .id(resource.getMetadata().getId())
        .name(resource.getEntity().getName())
        .build());
```

The above example is more complicated:

1. `.list(...)` – Retrieves a page of Cloud Foundry organizations.
1. `.flatMap(...)` – Substitutes the original `Mono` with a `Flux` of the `Resource`s returned by the requested page.
1. `.map(...)` – Maps the `Resource` to an `Organization` type.

### Maven Plugin

TODO: Document once implemented

### Gradle Plugin

TODO: Document once implemented

## Documentation
API Documentation for each module can be found at the following locations:

* `cloudfoundry-client` – [`release`](http://cloudfoundry.github.io/cf-java-client/api/latest-release/cloudfoundry-client), [`milestone`](http://cloudfoundry.github.io/cf-java-client/api/latest-milestone/cloudfoundry-client), [`snapshot`](http://cloudfoundry.github.io/cf-java-client/api/latest-snapshot/cloudfoundry-client)
* `cloudfoundry-client-reactor` – [`release`](http://cloudfoundry.github.io/cf-java-client/api/latest-release/cloudfoundry-client-reactor), [`milestone`](http://cloudfoundry.github.io/cf-java-client/api/latest-milestone/cloudfoundry-client-reactor), [`snapshot`](http://cloudfoundry.github.io/cf-java-client/api/latest-snapshot/cloudfoundry-client-reactor)
* `cloudfoundry-operations` – [`release`](http://cloudfoundry.github.io/cf-java-client/api/latest-release/cloudfoundry-operations), [`milestone`](http://cloudfoundry.github.io/cf-java-client/api/latest-milestone/cloudfoundry-operations), [`snapshot`](http://cloudfoundry.github.io/cf-java-client/api/latest-snapshot/cloudfoundry-operations)
* `cloudfoundry-util` – [`release`](http://cloudfoundry.github.io/cf-java-client/api/latest-release/cloudfoundry-util), [`milestone`](http://cloudfoundry.github.io/cf-java-client/api/latest-milestone/cloudfoundry-util), [`snapshot`](http://cloudfoundry.github.io/cf-java-client/api/latest-snapshot/cloudfoundry-util)

## Development
The project depends on Java 8.  To build from source and install to your local Maven cache, run the following:

```shell
$ ./mvnw clean install
```

To run the integration tests, run the following:

```
$ ./mvnw -Pintegration-test clean test
```

**IMPORTANT**
Integration tests should be run against an empty Cloud Foundry instance. The integration tests are destructive, affecting nearly everything on an instance given the chance.

The integration tests require a running instance of Cloud Foundry to test against.  We recommend using [PCF Dev][i] to start a local instance to test with.  To configure the integration tests with the appropriate connection information use the following environment variables:

Name | Description
---- | -----------
`TEST_APIHOST` | The host of Cloud Foundry instance.  Typically something like `api.local.pcfdev.io`.
`TEST_PASSWORD` | The test user's password
`TEST_SKIPSSLVALIDATION` | Whether to skip SSL validation when connecting to the Cloud Foundry instance.  Typically `true` when connecting to a PCF Dev instance.
`TEST_UAA_CLIENTID` | The client id to use for testing the UAA APIs
`TEST_UAA_CLIENTSECRET` | The client secret to use for testing the UAA APIs
`TEST_USERNAME` | The test user's username


## Contributing
[Pull requests][u] and [Issues][e] are welcome.

## License
This project is released under version 2.0 of the [Apache License][l].

[a]: https://apidocs.cloudfoundry.org/latest-release/
[c]: https://github.com/cloudfoundry/cli
[e]: https://github.com/cloudfoundry/cf-java-client/issues
[g]: https://gradle.org
[h]: http://projectreactor.io/io/docs/api/reactor/io/netty/http/HttpClient.html
[i]: https://github.com/pivotal-cf/pcfdev
[l]: https://www.apache.org/licenses/LICENSE-2.0
[m]: https://maven.apache.org
[p]: https://projectreactor.io
[r]: http://reactivex.io
[u]: https://help.github.com/articles/using-pull-requests
