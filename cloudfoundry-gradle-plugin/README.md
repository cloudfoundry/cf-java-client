GRADLE PLUGIN FOR CLOUD FOUNDRY
===============================

This plugin allows you to deploy and manage applications with Gradle tasks. It is intended as an alternative
to `cf` for deploying applications built with Gradle.

The plugin adds the following tasks:

* cf-target - Displays information about the target Cloud Foundry platform
* cf-login - Logs in then out to verify credentials
* cf-app - Displays information about the application deployment
* cf-apps - Lists applications running on the targeted Cloud Foundry platform
* cf-push - Pushes an application
* cf-start - Starts an application
* cf-stop - Stops an application
* cf-restart - Restarts an application
* cf-delete - Deletes an application deployment
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

Configuring
-----------

Configuration is either project based or task based. It is simpler to use project configuration. Here is a sample
Gradle project.

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'org.gradle.api.plugins', name: 'gradle-cf-plugin', version: '0.2.2-SNAPSHOT'
    }
}

apply plugin: 'cloudfoundry'

cloudfoundry {
    username = 'login@example.com'
    password = 's3cr3t'
    application = 'appName'
    file = new File('/path/to/app.war')
    uri = 'http://app-name.run.pivotal.io'
}
```

Then usage is simple:

This will deploy the application:
```gradle cf-push```

The configuration options are:
* target: the URL of the target API (http://api.run.pivotal.io by default)
* organization: the name of the Cloud Foundry organization to target
* space: the name of the Cloud Foundry space to target
* username: your username on the target platform
* password: your password on the target platform

* application: the name of your application (defaults to the Gradle project name)
* memory: amount of memory for an application in megabytes (defaults to 512)
* instances: number of instances (defaults to 1)
* uri: a URI to map to the application
* uris (type: List): a list of URIs to map to the application
* file (type: File): path to the WAR file to be deployed
* command: the command to run when the application is started
* buildpack: the URL of a buildpack to use to stage the application
* env (type: Map): environment variables to set for the application
* startApp (type: boolean): start the application on push (defaults to true)

* services: list of services the application uses

Services
--------

The configuration can contain information about services that should be bound to the application or otherwise
managed by tasks. One or more services are nested in the `cloudfoundry` configuration:

```
cloudfoundry {
    username = 'login@example.com'
    password = 's3cr3t'
    application = 'app-name'
    file = new File('/path/to/app.war')
    uri = 'http://app-name.run.pivotal.io'

    serviceInfos {
        'my-mongodb' {
            label = 'mongolab'
            provider = 'mongolab'
            version = 'n/a'
            plan = 'sandbox'
            bind = true
        }
    }
}
```

The service configuration options are:
* label: the type of the service
* provider: the name of the service vendor
* version: the version of the service
* plan: the tier option of the service
* bind (type: boolean): bind the service to the application on push (defaults to true)

Use `cf-service-plans` to see the valid values for service configuration.

Overriding properties from command line
---------------------------------------

In addition to the `build.gradle` based configuration, it is also possible to use command line options to set properties.
For example, to set the username and password on the command line instead of having them visible in build.gradle (assuming you set the cloudfoundry section
in the build.gradle file):

```gradle cf-target -Pcloudfoundry.username='login@example.com' -Pcloudfoundry.password='s3cr3t'```
