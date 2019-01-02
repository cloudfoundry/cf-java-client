cf-java-client
================

[![Build Status](https://travis-ci.org/cloudfoundry/cf-java-client.png)](https://travis-ci.org/cloudfoundry/cf-java-client)

The cf-java-client repo contains a Java client library and tools for Cloud Foundry. Three major components are included
in this repo.

# Components

## cloudfoundry-client-lib

The cloudfoundry-client-lib is a Java library that provides a Java language binding for the Cloud Foundry Cloud Controller REST API.
The library can be used by Java, Groovy, and Scala apps to interact with a Cloud Foundry service on behalf of a user.

[Read more](http://docs.cloudfoundry.org/buildpacks/java/java-client.html)

## cloudfoundry-maven-plugin

The Cloud Foundry Maven plugin is a plugin for the [Maven build tool](http://maven.apache.org/) that allows you to
deploy and manage applications with Maven goals.

[Read more](./cloudfoundry-maven-plugin)

## cloudfoundry-gradle-plugin

The Cloud Foundry Gradle plugin is a plugin for the [Gradle build tool](http://www.gradle.org/) that allows you to
deploy and manage applications with Gradle tasks.

[Read more](./cloudfoundry-gradle-plugin)

# Building

## Prerequisites

### Apache Maven

The `cloudfoundry-client-lib` and `cloudfoundry-maven-plugin` components are built with [Apache Maven](http://maven.apache.org/).

### Gradle

The `cloudfoundry-gradle-plugin` component is built with [Gradle](http://www.gradle.org/).

### Protocol Buffer compiler

The `cloudfoundry-client-lib` uses Protocol Buffers to get logs from the Cloud Foundry [loggregator](https://github.com/cloudfoundry/loggregator)
component. A `protoc` Protocol Buffer compiler is required at build time to compile message specifications. `protoc` version
2.6.1 is required.

On Linux with `apt`, run the [install-protoc-apt.sh](./bin/install-protoc-apt.sh) script in this repository to compile `protoc` from source.

NOTE: In case of error try to fix it with: `sudo ldconfig`

On OSX, run the [install-protoc-osx.sh](./bin/install-protoc-osx.sh) script to install `protoc`. Alternatively you can install `protobuf` using [homebrew](http://brew.sh/) if homebrew supports the appropriate version.

On Windows, download the `protoc` binary zip file from the [releases page](https://github.com/google/protobuf/releases),
unzip it, and put `protoc.exe` in the path.

After installing, run this command and check the output to make sure it is similar to the following:

```
$ protoc --version
libprotoc 2.6.1
```

## Compiling and Packaging

To build `cloudfoundry-client-lib` and `cloudfoundry-maven-plugin`, run the following command from the project root directory:

```
$ mvn clean install
```

To build `cloudfoundry-gradle-plugin`, run the following command from the `cloudfoundry-gradle-plugin` sub-directory after
building `cloudfoundry-client-lib`:

```
$ gradle clean install
```

## Running Integration Tests

`cloudfoundry-client-lib` has an extensive set of integration tests which run against a Cloud Foundry service. To execute the
integration tests, run the following command from the project root directory:

```
$ mvn -P integration-test clean install -Dccng.target=<endpoint> -Dccng.email=<username> -Dccng.passwd=<password> -Dccng.org=<organization> -Dccng.space=<space>
```

Following is a complete list of the `-D` parameters that can be passed to the integration test:

| Parameter        | Description                                                     | Required/Optional |
| ---------        | -----------                                                     | ----------------- |
| ccng.target      | target Cloud Foundry endpoint (e.g. https://api.run.pivotal.io) | required |
| ccng.email       | Cloud Foundry username                                          | required |
| ccng.passwd      | Cloud Foundry password                                          | required |
| ccng.org         | Cloud Foundry organization to run tests against                 | required |
| ccng.space       | Cloud Foundry space to run tests against                        | required |
| ccng.ssl         | trust self-signed certificates from target endpoint             | optional, default is `false` |
| vcap.mysql.label | label of a MySQL service that can be created                    | optional, default is `cleardb` |
| vcap.mysql.plan  | plan of a MySQL service that can be created                     | optional, default is `spark` |
| http.proxyHost   | host name of an HTTP proxy                                      | optional |
| http.proxyPort   | port of an HTTP proxy                                           | optional |

**Important**

Integration tests should be run against an empty Cloud Foundry space. The integration tests are destructive,
and will delete any apps, services, routes, and domains existing in the target space.

# Cloud Foundry Resources

_Cloud Foundry Open Source Platform as a Service_

## Learn

Our documentation, currently a work in progress, is available here: [http://cloudfoundry.github.com/](http://cloudfoundry.github.com/)

## Ask Questions

Questions about the Cloud Foundry Open Source Project can be directed to our Google Groups.

* Cloud Foundry Developers: [https://groups.google.com/a/cloudfoundry.org/group/vcap-dev/topics](https://groups.google.com/a/cloudfoundry.org/group/vcap-dev/topics)
* BOSH Developers: [https://groups.google.com/a/cloudfoundry.org/group/bosh-dev/topics](https://groups.google.com/a/cloudfoundry.org/group/bosh-dev/topics)
* BOSH Users:[https://groups.google.com/a/cloudfoundry.org/group/bosh-users/topics](https://groups.google.com/a/cloudfoundry.org/group/bosh-users/topics)

## File a bug

Bugs can be filed using Github Issues within the various repositories of the [Cloud Foundry](http://github.com/cloudfoundry) components.

## OSS Contributions

The Cloud Foundry team uses GitHub and accepts contributions via [pull request](https://help.github.com/articles/using-pull-requests)

Follow these steps to make a contribution to any of our open source repositories:

1. Complete our CLA Agreement for [individuals](http://www.cloudfoundry.org/individualcontribution.pdf) or [corporations](http://www.cloudfoundry.org/corpcontribution.pdf)
2. Set your name and email

```
$ git config --global user.name "Firstname Lastname"
$ git config --global user.email "your_email@youremail.com"
```

3. Fork the repo
4. Make your changes on a topic branch, commit, and push to github and open a pull request.

Once your commits are approved by Travis CI and reviewed by the core team, they will be merged.
