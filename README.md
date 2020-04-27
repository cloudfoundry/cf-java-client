# Cloud Foundry Java Client
[![Maven Central](https://img.shields.io/maven-central/v/org.cloudfoundry/cloudfoundry-client)](https://search.maven.org/search?q=g:org.cloudfoundry%20AND%20a:cloudfoundry-client%20AND%20v)

| Artifact | Javadocs
| -------- | --------
| `cloudfoundry-client`         | [![javadoc](https://javadoc.io/badge2/org.cloudfoundry/cloudfoundry-client/javadoc.svg)](https://javadoc.io/doc/org.cloudfoundry/cloudfoundry-client)
| `cloudfoundry-client-reactor` | [![javadoc](https://javadoc.io/badge2/org.cloudfoundry/cloudfoundry-client-reactor/javadoc.svg)](https://javadoc.io/doc/org.cloudfoundry/cloudfoundry-client-reactor)
| `cloudfoundry-operations`     | [![javadoc](https://javadoc.io/badge2/org.cloudfoundry/cloudfoundry-operations/javadoc.svg)](https://javadoc.io/doc/org.cloudfoundry/cloudfoundry-operations)
| `cloudfoundry-util`           | [![javadoc](https://javadoc.io/badge2/org.cloudfoundry/cloudfoundry-util/javadoc.svg)](https://javadoc.io/doc/org.cloudfoundry/cloudfoundry-util)

| Job | Status
| --- | ------
| `unit-test`             | [![unit-test-master](https://java-experience.ci.springapps.io/api/v1/teams/java-experience/pipelines/java-client/jobs/unit-test-master/badge)](https://java-experience.ci.springapps.io/teams/java-experience/pipelines/java-client/jobs/unit-test-master)
| `integration-test-2.5`  | [![integration-test-2.5-master](https://java-experience.ci.springapps.io/api/v1/teams/java-experience/pipelines/java-client/jobs/integration-test-2.5-master/badge)](https://java-experience.ci.springapps.io/teams/java-experience/pipelines/java-client/jobs/integration-test-2.5-master)
| `integration-test-2.6`  | [![integration-test-2.6-master](https://java-experience.ci.springapps.io/api/v1/teams/java-experience/pipelines/java-client/jobs/integration-test-2.6-master/badge)](https://java-experience.ci.springapps.io/teams/java-experience/pipelines/java-client/jobs/integration-test-2.6-master)
| `integration-test-2.7`  | [![integration-test-2.7-master](https://java-experience.ci.springapps.io/api/v1/teams/java-experience/pipelines/java-client/jobs/integration-test-2.7-master/badge)](https://java-experience.ci.springapps.io/teams/java-experience/pipelines/java-client/jobs/integration-test-2.7-master)
| `integration-test-2.8`  | [![integration-test-2.8-master](https://java-experience.ci.springapps.io/api/v1/teams/java-experience/pipelines/java-client/jobs/integration-test-2.8-master/badge)](https://java-experience.ci.springapps.io/teams/java-experience/pipelines/java-client/jobs/integration-test-2.8-master)
| `deploy`                | [![deploy-master](https://java-experience.ci.springapps.io/api/v1/teams/java-experience/pipelines/java-client/jobs/deploy-master/badge)](https://java-experience.ci.springapps.io/teams/java-experience/pipelines/java-client/jobs/deploy-master)


The `cf-java-client` project is a Java language binding for interacting with a Cloud Foundry instance.  The project is broken up into a number of components that expose different levels of abstraction depending on need.

* `cloudfoundry-client` – Interfaces, request, and response objects mapping to the [Cloud Foundry REST APIs][a].  This project has no implementation and therefore cannot connect to a Cloud Foundry instance on its own.
* `cloudfoundry-client-reactor` – The default implementation of the `cloudfoundry-client` project.  This implementation is based on Reactor Netty [`HttpClient`][h].
* `cloudfoundry-operations` – An API and implementation that corresponds to the [Cloud Foundry CLI][c] operations.  This project builds on the `cloudfoundry-client` and therefore has a single implementation.

## Versions
The Cloud Foundry Java Client has two active versions. The `4.x` line uses Spring Boot `2.2.x` just to manage its dependencies, while the `3.x` line uses Spring Boot `2.1.x`. Unless you have a specific dependency-related reason for using the older version we recommend you adopt the `4.x` line.

## Dependencies
Most projects will need two dependencies; the Operations API and an implementation of the Client API.  For Maven, the dependencies would be defined like this:

```xml
<dependencies>
    <dependency>
        <groupId>org.cloudfoundry</groupId>
        <artifactId>cloudfoundry-client-reactor</artifactId>
        <version>4.6.0.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.cloudfoundry</groupId>
        <artifactId>cloudfoundry-operations</artifactId>
        <version>4.6.0.RELEASE</version>
    </dependency>
    ...
</dependencies>
```

Snapshot artifacts can be found in the Spring snapshot repository:

```xml
<repositories>
    <repository>
        <id>spring-snapshots</id>
        <name>Spring Snapshots</name>
        <url>https://repo.spring.io/snapshot</url>
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
    compile 'org.cloudfoundry:cloudfoundry-client-reactor:4.6.0.RELEASE'
    compile 'org.cloudfoundry:cloudfoundry-operations:4.6.0.RELEASE'
    ...
}
```

Snapshot artifacts can be found in the Spring snapshot repository:

```groovy
repositories {
    maven { url 'https://repo.spring.io/snapshot' }
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

```java
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

```java
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
1. Prints the name of each organization to `System.out`

```java
cloudFoundryOperations.organizations()
    .list()
    .map(OrganizationSummary::getName)
    .subscribe(System.out::println);
```

To relate the example to the description above the following happens:

1. `.list()` – Lists the Cloud Foundry organizations as a `Flux` of elements of type `Organization`.
1. `.map(...)` – Maps each organization to its name (type `String`).  This example uses a method reference; the equivalent lambda would look like `organizationSummary -> organizationSummary.getName()`.
1. `subscribe...` – The terminal operation that receives each name in the `Flux`.  Again, this example uses a method reference and the equivalent lambda would look like `name -> System.out.println(name)`.

### `CloudFoundryClient` APIs

As mentioned earlier, the `cloudfoundry-operations` implementation builds upon the `cloudfoundry-client` API.  That implementation takes advantage of the same reactive style in the lower-level API.  The implementation of the `Organizations.list()` method (which was demonstrated above) looks like the following (roughly):

```java
cloudFoundryClient.organizations()
    .list(ListOrganizationsRequest.builder()
        .page(1)
        .build())
    .flatMapIterable(ListOrganizationsResponse::getResources)
    .map(resource -> OrganizationSummary.builder()
        .id(resource.getMetadata().getId())
        .name(resource.getEntity().getName())
        .build());
```

The above example is more complicated:

1. `.list(...)` – Retrieves the first page of Cloud Foundry organizations.
1. `.flatMapIterable(...)` – Substitutes the original `Mono` with a `Flux` of the `Resource`s returned by the requested page.
1. `.map(...)` – Maps the `Resource` to an `OrganizationSummary` type.

## Development
The project depends on Java 8. To build from source and install to your local Maven cache, run the following:

```shell
$ git submodule update --init --recursive
$ ./mvnw clean install
```

It also depends on [Immutables][i] and won't compile in IDEs like Eclipse or IntelliJ unless you also have an enabled annotation processor. See [this guide][j] for instructions on how to configure your IDE.

To run the integration tests, run the following:

```shell
$ ./mvnw -Pintegration-test clean test
```

**IMPORTANT**
Integration tests should be run against an empty Cloud Foundry instance. The integration tests are destructive, affecting nearly everything on an instance given the chance.

The integration tests require a running instance of Cloud Foundry to test against. To configure the integration tests with the appropriate connection information use the following environment variables:

Name | Description
---- | -----------
`TEST_ADMIN_CLIENTID` | Client ID for a client with permissions for a Client Credentials grant
`TEST_ADMIN_CLIENTSECRET` | Client secret for a client with permissions for a Client Credentials grant
`TEST_ADMIN_PASSWORD` | Password for a user with admin permissions
`TEST_ADMIN_USERNAME` | Username for a user with admin permissions
`TEST_APIHOST` | The host of a Cloud Foundry instance.  Typically something like `api.local.pcfdev.io`.
`TEST_PROXY_HOST` | _(Optional)_ The host of a proxy to route all requests through
`TEST_PROXY_PASSWORD` | _(Optional)_ The password for a proxy to route all requests through
`TEST_PROXY_PORT` | _(Optional)_ The port of a proxy to route all requests through. Defaults to `8080`.
`TEST_PROXY_USERNAME` | _(Optional)_ The username for a proxy to route all requests through
`TEST_SKIPSSLVALIDATION` | _(Optional)_ Whether to skip SSL validation when connecting to the Cloud Foundry instance.  Defaults to `false`.

## Contributing
[Pull requests][u] and [Issues][e] are welcome.

## License
This project is released under version 2.0 of the [Apache License][l].

[a]: https://apidocs.cloudfoundry.org/latest-release/
[c]: https://github.com/cloudfoundry/cli
[e]: https://github.com/cloudfoundry/java-client/issues
[g]: https://gradle.org
[h]: https://projectreactor.io/docs/netty/milestone/reference/index.html#http-client
[i]: https://immutables.github.io/
[j]: https://immutables.github.io/apt.html
[l]: https://www.apache.org/licenses/LICENSE-2.0
[m]: https://maven.apache.org
[p]: https://projectreactor.io
[r]: http://reactivex.io
[u]: https://help.github.com/articles/using-pull-requests
