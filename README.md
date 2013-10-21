cf-java-client
================

[![Build Status](https://travis-ci.org/cloudfoundry/cf-java-client.png)](https://travis-ci.org/cloudfoundry/cf-java-client)

The cf-java-client repo contains a Java client library and tools for Cloud Foundry. Three major components are included
in this repo.

# Components

## cloudfoundry-client-lib

The cloudfoundry-client-lib is a Java library that provides a Java language binding for the Cloud Foundry Cloud Controller REST API.
The library can be used by Java, Groovy, and Scala apps to interact with a Cloud Foundry service on behalf of a user.

[Read more](http://docs.cloudfoundry.com/docs/using/managing-apps/libs/java-client.html)

## cloudfoundry-maven-plugin

The Cloud Foundry Maven plugin is a plugin for the [Maven build tool](http://maven.apache.org/) that allows you to deploy and manage applications with Maven goals.

[Read more](./cloudfoundry-maven-plugin)

## cloudfoundry-gradle-plugin

The Cloud Foundry Gradle plugin is a plugin for the [Gradle build tool](http://www.gradle.org/) that allows you to deploy and manage applications with Gradle tasks.

[Read more](./cloudfoundry-gradle-plugin)

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
