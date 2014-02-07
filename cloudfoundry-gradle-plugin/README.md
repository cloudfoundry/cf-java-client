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
        classpath group: 'org.cloudfoundry', name: 'cf-gradle-plugin', version: '1.0.2'
    }
}

apply plugin: 'cloudfoundry'

~~~

## Using the plugin

The Cloud Foundry Gradle plugin adds several tasks to the build. Specify one or more of these tasks on the `gradle` command line:

~~~
$ gradle cf-push
~~~

The plugin adds the following tasks:

* cf-target - Displays information about the target Cloud Foundry platform
* cf-app - Displays information about the application deployment
* cf-apps - Lists applications running on the targeted Cloud Foundry platform
* cf-push - Pushes an application
* cf-delete - Deletes an application deployment
* cf-start - Starts an application
* cf-stop - Stops an application
* cf-restart - Restarts an application
* cf-scale - Scales application instances up or down
* cf-services - Displays information about service instances
* cf-service-plans - Displays information about available service offerings
* cf-create-service - Creates a service, optionally binding it to an application
* cf-delete-service - Deletes a service
* cf-bind - Binds a service to an application
* cf-unbind - Unbinds a service from an application
* cf-env - List application environment variables
* cf-set-env - Sets environment variables to an application
* cf-unset-env - Deletes environment variables from an application
* cf-map - Maps a URI to an application
* cf-unmap - Unmaps a URI from application
* cf-login - Logs in and saves authentication tokens
* cf-logout - Logs out and removes authentication tokens

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
* file (type: File) - path to the JAR or WAR file to be deployed
* memory - amount of memory in megabytes to allocate to an application
* diskQuota - amount of disk space in megabytes to allocate to an application
* healthCheckTimeout - the amount of time in seconds that Cloud Foundry should wait for the application to start
* instances - number of instances (defaults to 1)
* uri - a URI to map to the application
* uris (type: List) - a list of URIs to map to the application
* host - combined with `domain` to specify a URI to map to the application
* hosts (type: List) - combined with `domain` to specify a list of URIs to map to the application
* domain - the domain part of URIs to map to the application
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

Use the `cf-service-plans` task to see the valid values for service configuration.

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

userProvidedCredentials = [
                    'uri': 'mysql://b56d0d229a2478:f98e2c5c@us-cdbr-east-04.cleardb.com:3306/ad_fb30a5b6060fc84?reconnect=true',
                    'port': 12345
            ]

### Overriding configuration from command line

In addition to the `build.gradle` file-based configuration, it is also possible to use command line options to set
properties. To set options from the command line, use the Gradle `-P` flag and prefix property names with `cf`. For
example, to set the username and password on the command line instead of having them visible in `build.gradle`:

~~~
$ gradle cf-target -Pcf.username='user@example.com' -Pcf.password='s3cr3t'
~~~

## Authentication

The Cloud Foundry username and password can be set in the `cloudfoundry` configuration section in `build.gradle`, but
storing passwords in a build file like this is not usually a good idea. There are a few different ways you can keep
from storing login credentials in `build.gradle`.

### Passing credentials on the command line

Login credentials can be passed as command-line properties with each invoked task, as show above:

~~~
$ gradle cf-target -Pcf.username='user@example.com' -Pcf.password='s3cr3t' -Pcf.space='staging'
~~~

### Configuring credentials in `gradle.properties`

The `gradle.properties` file can be used to store properties as an alternative to passing them on the command line. This
file can be placed in a user's home directory (e.g. `~/.gradle/gradle.properties`) or in your project directory.

An example `gradle.properties` file might look like this:

~~~
cf.username='user@example.com'
cf.password='s3cr3t'
~~~

### Using saved tokens

The `cf-login` task can be used to log into Cloud Foundry and save the authentication tokens to a file. After logging in
with `cf-login`, the username and password will not be required for other tasks.

~~~
$ gradle cf-login -Pcf.username='user@example.com' -Pcf.password='s3cr3t' -Pcf.space='staging'
~~~

The `cf-logout` task can be used to clear the saved tokens:

~~~
$ gradle cf-logout
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
$ gradle cf-push -Pdev
$ gradle cf-push -Pprod
~~~

# History

## Changes in 1.0.2

* Added HTTP Proxy support for targeting CF platforms from behind a proxy
* Added support for user-provided service instances
* Added support for `healthCheckTimeout` and `diskQuota` application parameters
* Removed defaulting of `memory` app setting to prefer the default configured in Cloud Controller
* Added `trustSelfSignedCerts` plugin parameter