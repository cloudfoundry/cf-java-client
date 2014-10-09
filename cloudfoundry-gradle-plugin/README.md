Cloud Foundry Gradle Plugin
===========================

This plugin allows you to deploy and manage applications with Gradle tasks. It is intended as an alternative
to `cf` for managing applications built with Gradle.

## Installing the plugin

To install the Cloud Foundry Gradle plugin, add it as a dependency in the `buildscript` section of your `build.gradle` file:

~~~
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'org.cloudfoundry', name: 'cf-gradle-plugin', version: '1.0.4'
    }
}

apply plugin: 'cloudfoundry'

~~~

## Using the plugin

The Cloud Foundry Gradle plugin adds several tasks to the build. Specify one or more of these tasks on the `gradle` command line:

~~~
$ gradle cfPush
~~~

The plugin adds the following tasks:

* cfTarget - Displays information about the target Cloud Foundry platform
* cfApp - Displays information about the application deployment
* cfApps - Lists applications running on the targeted Cloud Foundry platform
* cfPush - Pushes an application
* cfDelete - Deletes an application deployment
* cfStart - Starts an application
* cfStop - Stops an application
* cfRestart - Restarts an application
* cfScale - Scales application instances up or down
* cfLogs - Tails application logs
* cfRecentLogs - Displays recent application logs
* cfServices - Displays information about service instances
* cfServicePlans - Displays information about available service offerings
* cfCreateService - Creates a service, optionally binding it to an application
* cfDeleteService - Deletes a service
* cfBind - Binds a service to an application
* cfUnbind - Unbinds a service from an application
* cfEnv - List application environment variables
* cfSetEnv - Sets environment variables to an application
* cfUnsetEnv - Deletes environment variables from an application
* cfMap - Maps a URI to an application
* cfUnmap - Unmaps a URI from application
* cfDeleteOrphanedRoutes - Deletes all routes that are not assigned to any applications
* cfLogin - Logs in and saves authentication tokens
* cfLogout - Logs out and removes authentication tokens

## Configuring the plugin

The Cloud Foundry Gradle plugin is configured using a `cloudfoundry` configuration section in `build.gradle`:

~~~
cloudfoundry {
    target = 'https://api.run.pivotal.io'
    space = 'development'
    username = 'user@example.com'
    password = 's3cr3t'
    file = file("${war.archivePath}")
    uri = 'http://my-app.run.pivotal.io'
    env = [
        "key": "value"
    ]
}
~~~

Configuration options are:
* target - the URL of the target API (defaults to http://api.run.pivotal.io)
* organization - the name of the Cloud Foundry organization to target
* space - the name of the Cloud Foundry space to target
* username - your username on the target platform
* password - your password on the target platform
* application - the name of your application (defaults to the Gradle project name)
* file (type: File) - path to the JAR or WAR file to be deployed (defaults to the primary build artifact)
* memory - amount of memory in megabytes to allocate to an application
* diskQuota - amount of disk space in megabytes to allocate to an application
* healthCheckTimeout - the amount of time in seconds that Cloud Foundry should wait for the application to start
* instances - number of instances (defaults to 1)
* uri - a URI to map to the application
* uris (type: List) - a list of URIs to map to the application
* host - combined with `domain` to specify a URI to map to the application (defaults to application name)
* hosts (type: List) - combined with `domain` to specify a list of URIs to map to the application
* domain - the domain part of URIs to map to the application (defaults to the default domain)
* command - the command to run when the application is started
* buildpack - the URL of a buildpack to use to stage the application
* env (type: Map) - environment variables to set for the application
* startApp (type: boolean) - start the application on push (defaults to true)

### Configuring services

The configuration can contain information about system-provisioned services that should be bound to the application or otherwise
managed by tasks. One or more services can be nested in the `cloudfoundry` configuration:

~~~
cloudfoundry {
    username = 'user@example.com'
    password = 's3cr3t'
    application = 'app-name'
    file = new File('build/libs/my-app.war')
    uri = 'http://app-name.run.pivotal.io'

    services {
        'my-mongodb' {
            label = 'mongolab'
            plan = 'sandbox'
            bind = true
        }
    }
}
~~~

The configuration options for system-provisioned services are:
* label - the type of the service
* plan - the tier option of the service
* bind (type: boolean) - bind the service to the application on push (defaults to true)

Use the `cfServicePlans` task to see the valid values for service configuration.

The configuration can also contain information about user-provided service instances, where service credentials are
specified by the user. In this case, the `label` must be `user-provided`.

~~~
cloudfoundry {
    username = 'user@example.com'
    password = 's3cr3t'
    application = 'app-name'
    file = new File('build/libs/my-app.war')
    uri = 'http://app-name.run.pivotal.io'

    services {
        'my-web-service' {
            label = 'user-provided'
            userProvidedCredentials = [
                'uri': 'http://example.com/service',
                'accessKey': 'abc123'
            ]
            bind = true
        }
    }
}
~~~

### Overriding configuration from command line

In addition to the `build.gradle` file-based configuration, it is also possible to use command line options to set
properties. To set options from the command line, use the Gradle `-P` flag and prefix property names with `cf`. For
example, to set the username and password on the command line instead of having them visible in `build.gradle`:

~~~
$ gradle cfTarget -PcfUsername='user@example.com' -PcfPassword='s3cr3t'
~~~

## Authentication

The Cloud Foundry username and password can be set in the `cloudfoundry` configuration section in `build.gradle`, but
storing passwords in a build file like this is not usually a good idea. There are a few different ways you can keep
from storing login credentials in `build.gradle`.

### Passing credentials on the command line

Login credentials can be passed as command-line properties with each invoked task, as show above:

~~~
$ gradle cfTarget -PcfUsername='user@example.com' -PcfPassword='s3cr3t' -PcfSpace='staging'
~~~

### Configuring credentials in `gradle.properties`

The `gradle.properties` file can be used to store properties as an alternative to passing them on the command line. This
file can be placed in a user's home directory (e.g. `~/.gradle/gradle.properties`) or in your project directory.

An example `gradle.properties` file might look like this:

~~~
cfUsername='user@example.com'
cfPassword='s3cr3t'
~~~

### Using saved tokens

The `cfLogin` task can be used to log into Cloud Foundry and save the authentication tokens to a file. After logging in
with `cfLogin`, the username and password will not be required for other tasks.

~~~
$ gradle cfLogin -PcfUsername='user@example.com' -PcfPassword='s3cr3t' -PcfSpace='staging'
~~~

The `cfLogout` task can be used to clear the saved tokens:

~~~
$ gradle cfLogout
~~~

The tokens are saved in a way that is compatible with the `cf` command-line tool, so you can also use `cf login` and then
run other tasks without providing credentials.

## Advanced configuration

### HTTP Proxies

By default, the Cloud Foundry Gradle Plugin will honor the Gradle HTTP proxy configuration when communicating with the
target Cloud Foundry service. See http://www.gradle.org/docs/current/userguide/userguide_single.html#sec:accessing_the_web_via_a_proxy
for more information on configuring Gradle to use an HTTP proxy.

If Gradle needs a proxy to resolve build dependencies but you do not want to use the proxy to communicate to the target
Cloud Foundry service, include the configuration setting `useSystemProxy=false` in your Cloud Foundry plugin configuration.

### Self-signed SSL Certificates

Some Cloud Foundry deployments, such as those deployed using Pivotal CF, use a self-signed certificate for SSL
connectivity. If you attempt to target a Cloud Foundry service that is using self-signed certificates, you may get an
error containing the text `javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated`.

To instruct the Cloud Foundry Gradle plugin to accept self-signed certificates from the Cloud Foundry target endpoint,
add `trustSelfSignedCerts=true` to the plugin configuration block.

### Configuration variables

#### randomWord

The `${randomWord}` variable is replaced with a randomly-generated set of characters. This can be useful to make application
URIs and service names unique:

~~~
cloudfoundry {
    ...
    uri = "http://app-name-${randomWord}.run.pivotal.io"
    ...

    serviceInfos {
        "mongodb-${randomWord}" {
            ...
        }
    }
}
~~~

### Conditional configuration based on properties

Command-line properties can be used to conditionally set configuration options in `build.gradle`. This can be used to
support multiple Cloud Foundry targets from a single `build.gradle` file, to select an organization and space, or to
customize other configuration options.

Selecting an organization and space based on a property might like like this:

~~~
if (project.hasProperty('dev')) {
    cloudfoundry {
        organization = 'my-org'
        space = 'development'
    }
}

if (project.hasProperty('staging')) {
    cloudfoundry {
        organization = 'my-org'
        space = 'staging'
    }
}

if (project.hasProperty('prod')) {
    cloudfoundry {
        organization = 'production-org'
        space = 'production'
    }
}

cloudfoundry {
    target = 'https://api.run.pivotal.io'
    application = 'my-app'
    file = file('build/libs/my-app.war')
    ...
}
~~~

The organization and space would then be selected from the command line:

~~~
$ gradle cfPush -Pdev
$ gradle cfPush -Pprod
~~~

### Zero-downtime deployment

The Cloud Foundry Gradle plugin has support for zero-downtime deployments using the [blue-green deployment technique](http://docs.cloudfoundry.org/devguide/deploy-apps/blue-green.html). 

These tasks should be considered *experimental*. Zero-downtime deployment features are planned for future versions of Cloud Foundry, at which time the semantics and behavior of these tasks will be adapted to the platform's capabilities as appropriate. 

The zero-downtime deployment tasks rely on a `variants` field being configured in the CF Gradle plugin configuration: 

~~~
cloudfoundry {
    target = "https://api.run.pivotal.io"
    organization = "my-org"
    space = "development"

    file = file("${war.archivePath}")
    host = "my-app"
    domain = "cfapps.io"
    memory = 512
    instances = 1

    variants = ['-blue', '-green']
}
~~~

Three additional Gradle tasks can be used to manage the deployment: 

* cfDeploy

The plugin detects which `variant` is currently running and mapped to the canonical route (determined by combining `host` and `domain`), and pushes the app using the “other” variant. Both the application name and the route are decorated with the chosen variant string. If `my-app-blue` is running and mapped to the well-known route (e.g. `my-app.cfapps.io`) then the app is pushed with the name `my-app-green` and mapped to `my-app-green.cfapps.io`. If no version is currently running or mapped to the canonical route, the first variant in the list is used. 

Multiple routes can be assigned to an application using the `hosts = [‘my-app’, ‘www-my-app’]` syntax. In this case, the decoration of routes and mapping/unmapping applies to each route.

The plugin also allows setting routes using `uri` and `uris` fields. These fields can be used with the deployment tasks, but will not be decorated with the variant values. Using `host` and `domain` together with `uri` give a high degree of control over how routes are configured.

After deployment, the new variant can then be tested using the decorated URL.

* cfSwapDeployed

Variants that are not currently mapped to the canonical `my-app.cfapps.io` route are mapped to it. Variants that are currently mapped to `my-app.cfapps.io` have that route removed from them. This effectively swaps the newer variant into service and the older variant out of service.

* cfUndeploy

All running variants that are not mapped to the canonical route are deleted. 

# History

## Changes in 1.0.4

* Added `cfDeleteOrphanedRoutes` goal
* Added a `currentVariant` property to expose the variant being used in a zero-downtime deployment  

## Changes in 1.0.3

* Tasks were renamed to use the form "cfTask" instead of "cf-task"
* Properties were changed to use the form "cfProperty" instead of "cf.property"
* Changed "cfLogs" task to tail logs from Loggregator. Added "cfRecentLogs" task
* The "host", "domain", and "url" configuration properties are now optional (defaults to a app name and default domain)
* The "file" configuration property is not optional (defaults to the build artifact)
* Plugin now supports uploading a directory or a single file

## Changes in 1.0.2

* Added HTTP Proxy support for targeting CF platforms from behind a proxy
* Added support for user-provided service instances
* Added support for `healthCheckTimeout` and `diskQuota` application parameters
* Removed defaulting of `memory` app setting to prefer the default configured in Cloud Controller
* Added `trustSelfSignedCerts` plugin parameter
